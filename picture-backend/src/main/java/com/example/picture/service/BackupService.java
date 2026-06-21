package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private TagService tagService;

    @Value("${upload.path:/app/images/}")
    private String uploadPath;

    @Value("${backup.path:/app/backups/}")
    private String backupPath;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public byte[] exportAlbum(Long albumId, String quality, String format, Long userId) throws IOException {
        Album album = albumRepository.findByIdAndUserId(albumId, userId).orElse(null);
        if (album == null) {
            album = albumRepository.findById(albumId).orElse(null);
            if (album == null) {
                throw new RuntimeException("专辑不存在");
            }
        }

        List<Picture> pictures = pictureRepository.findByAlbumIdAndUserId(albumId, userId);
        if (pictures.isEmpty()) {
            throw new RuntimeException("专辑中没有图片");
        }

        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String zipName = sanitizeFileName(album.getName()) + "_" + dateStr + ".zip";

        return createZipFromPictures(pictures, quality, format, zipName, false);
    }

    public byte[] exportPictures(List<Long> pictureIds, String quality, String format, Long userId) throws IOException {
        if (pictureIds == null || pictureIds.isEmpty()) {
            throw new RuntimeException("请选择要导出的图片");
        }

        List<Picture> pictures = new ArrayList<>();
        for (Long pid : pictureIds) {
            Picture pic = pictureRepository.findById(pid).orElse(null);
            if (pic != null && pic.getUserId().equals(userId) && !Boolean.TRUE.equals(pic.getDeleted())) {
                pictures.add(pic);
            }
        }

        if (pictures.isEmpty()) {
            throw new RuntimeException("没有可导出的图片");
        }

        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String zipName = "pictures_" + dateStr + ".zip";

        return createZipFromPictures(pictures, quality, format, zipName, false);
    }

    @Transactional
    public BackupDTO createFullBackup(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        Backup backup = new Backup();
        backup.setUserId(userId);
        backup.setStatus("PROCESSING");

        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + user.getUsername() + "_" + dateStr + ".zip";
        String filePath = backupPath + fileName;

        backup.setFileName(fileName);
        backup.setFilePath(filePath);

        Backup saved = backupRepository.save(backup);

        new Thread(() -> {
            try {
                doFullBackup(saved.getId(), userId, user.getUsername());
            } catch (Exception e) {
                updateBackupStatus(saved.getId(), "FAILED", e.getMessage());
            }
        }).start();

        return toDTO(saved);
    }

    private void doFullBackup(Long backupId, Long userId, String username) throws IOException {
        List<Picture> pictures = pictureRepository.findByUserIdOrderByCreateTimeDesc(userId);
        List<Album> albums = albumRepository.findByUserIdOrderByDisplayOrderAscCreateTimeAsc(userId);
        List<Tag> tags = tagRepository.findByUserIdOrderByReferenceCountDescCreateTimeDesc(userId);

        BackupMetadata metadata = new BackupMetadata();
        metadata.setVersion("1.0");
        metadata.setBackupTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        metadata.setUsername(username);

        List<BackupMetadata.BackupPicture> picList = new ArrayList<>();
        for (Picture pic : pictures) {
            BackupMetadata.BackupPicture bp = new BackupMetadata.BackupPicture();
            bp.setName(pic.getName());
            bp.setFileName(pic.getUrl().replace("/images/", ""));
            bp.setSize(pic.getSize());
            bp.setCreateTime(pic.getCreateTime() != null ? pic.getCreateTime().toString() : null);
            bp.setUpdateTime(pic.getUpdateTime() != null ? pic.getUpdateTime().toString() : null);
            bp.setIsPublic(pic.getIsPublic());

            List<String> tagNames = new ArrayList<>();
            for (Tag tag : pic.getTags()) {
                tagNames.add(tag.getName());
            }
            bp.setTags(tagNames);

            List<String> albumNames = new ArrayList<>();
            for (Album album : pic.getAlbums()) {
                albumNames.add(album.getName());
            }
            bp.setAlbums(albumNames);

            picList.add(bp);
        }
        metadata.setPictures(picList);

        List<BackupMetadata.BackupAlbum> albumList = new ArrayList<>();
        for (Album album : albums) {
            BackupMetadata.BackupAlbum ba = new BackupMetadata.BackupAlbum();
            ba.setName(album.getName());
            ba.setDescription(album.getDescription());
            ba.setDisplayOrder(album.getDisplayOrder());
            if (album.getCoverPictureId() != null) {
                Picture coverPic = pictureRepository.findById(album.getCoverPictureId()).orElse(null);
                if (coverPic != null) {
                    ba.setCoverPictureName(coverPic.getName());
                }
            }

            List<String> picNames = new ArrayList<>();
            for (Picture pic : album.getPictures()) {
                if (!Boolean.TRUE.equals(pic.getDeleted())) {
                    picNames.add(pic.getName());
                }
            }
            ba.setPictureNames(picNames);
            albumList.add(ba);
        }
        metadata.setAlbums(albumList);

        List<BackupMetadata.BackupTag> tagList = new ArrayList<>();
        for (Tag tag : tags) {
            BackupMetadata.BackupTag bt = new BackupMetadata.BackupTag();
            bt.setName(tag.getName());
            bt.setReferenceCount(tag.getReferenceCount());
            tagList.add(bt);
        }
        metadata.setTags(tagList);

        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + username + "_" + dateStr + ".zip";
        String filePath = backupPath + fileName;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filePath))) {
            ZipEntry metadataEntry = new ZipEntry("metadata.json");
            zos.putNextEntry(metadataEntry);
            zos.write(objectMapper.writeValueAsBytes(metadata));
            zos.closeEntry();

            ZipEntry picturesDir = new ZipEntry("pictures/");
            zos.putNextEntry(picturesDir);
            zos.closeEntry();

            int count = 0;
            for (Picture pic : pictures) {
                if (Boolean.TRUE.equals(pic.getDeleted())) continue;
                String picFileName = pic.getUrl().replace("/images/", "");
                File picFile = new File(uploadPath + picFileName);
                if (picFile.exists()) {
                    String entryName = "pictures/" + sanitizeFileName(pic.getName());
                    if (entryName.lastIndexOf('.') == -1 && picFileName.lastIndexOf('.') > 0) {
                        entryName += picFileName.substring(picFileName.lastIndexOf('.'));
                    }
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    Files.copy(picFile.toPath(), zos);
                    zos.closeEntry();
                    count++;
                }
            }

            long fileSize = new File(filePath).length();

            Backup backup = backupRepository.findById(backupId).orElse(null);
            if (backup != null) {
                backup.setFileName(fileName);
                backup.setFilePath(filePath);
                backup.setFileSize(fileSize);
                backup.setPictureCount(count);
                backup.setAlbumCount(albums.size());
                backup.setTagCount(tags.size());
                backup.setStatus("COMPLETED");
                backup.setCompleteTime(new Date());
                backupRepository.save(backup);
            }
        }
    }

    private void updateBackupStatus(Long backupId, String status, String errorMessage) {
        Backup backup = backupRepository.findById(backupId).orElse(null);
        if (backup != null) {
            backup.setStatus(status);
            backup.setErrorMessage(errorMessage);
            backup.setCompleteTime(new Date());
            backupRepository.save(backup);
        }
    }

    private byte[] createZipFromPictures(List<Picture> pictures, String quality, String format, String zipName, boolean includeMetadata) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Set<String> usedNames = new HashSet<>();

            for (Picture pic : pictures) {
                if (Boolean.TRUE.equals(pic.getDeleted())) continue;

                String picFileName = pic.getUrl().replace("/images/", "");
                File picFile = new File(uploadPath + picFileName);
                if (!picFile.exists()) continue;

                String entryName = sanitizeFileName(pic.getName());
                String ext = "";
                if (picFileName.lastIndexOf('.') > 0) {
                    ext = picFileName.substring(picFileName.lastIndexOf('.'));
                }
                if (entryName.lastIndexOf('.') == -1) {
                    entryName += ext;
                }

                String baseName = entryName;
                int counter = 1;
                while (usedNames.contains(entryName)) {
                    int dotIdx = baseName.lastIndexOf('.');
                    if (dotIdx > 0) {
                        entryName = baseName.substring(0, dotIdx) + "_" + counter + baseName.substring(dotIdx);
                    } else {
                        entryName = baseName + "_" + counter;
                    }
                    counter++;
                }
                usedNames.add(entryName);

                if ("compressed".equals(quality) || "jpg".equalsIgnoreCase(format)) {
                    byte[] processedImage = processImage(picFile, quality, format);
                    if ("jpg".equalsIgnoreCase(format)) {
                        int dotIdx = entryName.lastIndexOf('.');
                        if (dotIdx > 0) {
                            entryName = entryName.substring(0, dotIdx) + ".jpg";
                        } else {
                            entryName += ".jpg";
                        }
                    }
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    zos.write(processedImage);
                    zos.closeEntry();
                } else {
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    Files.copy(picFile.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }

        return baos.toByteArray();
    }

    private byte[] processImage(File imageFile, String quality, String format) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            return Files.readAllBytes(imageFile.toPath());
        }

        int targetMaxSize = 1920;
        int width = image.getWidth();
        int height = image.getHeight();

        if ("compressed".equals(quality) && (width > targetMaxSize || height > targetMaxSize)) {
            double ratio;
            if (width >= height) {
                ratio = (double) targetMaxSize / width;
            } else {
                ratio = (double) targetMaxSize / height;
            }
            int newWidth = (int) (width * ratio);
            int newHeight = (int) (height * ratio);

            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
            g2d.dispose();
            image = resized;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String outputFormat = "jpg".equalsIgnoreCase(format) ? "jpg" : getFormatFromFile(imageFile.getName());

        if ("jpg".equalsIgnoreCase(outputFormat) || "jpeg".equalsIgnoreCase(outputFormat)) {
            if (image.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = rgbImage.createGraphics();
                g2d.drawImage(image, 0, 0, Color.WHITE, null);
                g2d.dispose();
                image = rgbImage;
            }

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality("compressed".equals(quality) ? 0.85f : 0.95f);
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                    writer.setOutput(ios);
                    writer.write(null, new IIOImage(image, null, null), param);
                }
                writer.dispose();
            } else {
                ImageIO.write(image, "jpg", baos);
            }
        } else {
            ImageIO.write(image, outputFormat, baos);
        }

        return baos.toByteArray();
    }

    private String getFormatFromFile(String fileName) {
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0 && dotIdx < fileName.length() - 1) {
            return fileName.substring(dotIdx + 1).toLowerCase();
        }
        return "png";
    }

    private String sanitizeFileName(String name) {
        if (name == null) return "unknown";
        String sanitized = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        return sanitized;
    }

    public List<BackupDTO> listBackups(Long userId) {
        List<Backup> backups = backupRepository.findByUserIdOrderByCreateTimeDesc(userId);
        List<BackupDTO> result = new ArrayList<>();
        for (Backup backup : backups) {
            result.add(toDTO(backup));
        }
        return result;
    }

    public BackupDTO getBackup(Long id, Long userId) {
        Backup backup = backupRepository.findByIdAndUserId(id, userId);
        if (backup == null) {
            throw new RuntimeException("备份不存在");
        }
        return toDTO(backup);
    }

    public File getBackupFile(Long id, Long userId) {
        Backup backup = backupRepository.findByIdAndUserId(id, userId);
        if (backup == null) {
            throw new RuntimeException("备份不存在");
        }
        if (!"COMPLETED".equals(backup.getStatus())) {
            throw new RuntimeException("备份尚未完成");
        }
        File file = new File(backup.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("备份文件不存在");
        }
        return file;
    }

    @Transactional
    public void deleteBackup(Long id, Long userId) {
        Backup backup = backupRepository.findByIdAndUserId(id, userId);
        if (backup == null) {
            throw new RuntimeException("备份不存在");
        }
        File file = new File(backup.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        backupRepository.delete(backup);
    }

    @Transactional
    public RestoreResultDTO restoreBackup(MultipartFile file, String strategy, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请上传备份文件");
        }

        Path tempDir = Files.createTempDirectory("restore_" + System.currentTimeMillis());
        try {
            Path zipFile = tempDir.resolve("backup.zip");
            file.transferTo(zipFile.toFile());

            Path extractDir = tempDir.resolve("extract");
            Files.createDirectories(extractDir);

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path entryPath = extractDir.resolve(entry.getName()).normalize();
                    if (!entryPath.startsWith(extractDir)) {
                        continue;
                    }
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (OutputStream os = Files.newOutputStream(entryPath)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                os.write(buffer, 0, len);
                            }
                        }
                    }
                    zis.closeEntry();
                }
            }

            Path metadataFile = extractDir.resolve("metadata.json");
            if (!Files.exists(metadataFile)) {
                throw new RuntimeException("备份文件格式错误：缺少metadata.json");
            }

            BackupMetadata metadata = objectMapper.readValue(metadataFile.toFile(), BackupMetadata.class);

            Path picturesDir = extractDir.resolve("pictures");
            if (!Files.exists(picturesDir)) {
                throw new RuntimeException("备份文件格式错误：缺少pictures目录");
            }

            RestoreResultDTO result = new RestoreResultDTO();
            int restoredPictures = 0;
            int restoredAlbums = 0;
            int restoredTags = 0;
            int skippedPictures = 0;

            Map<String, Picture> picNameMap = new HashMap<>();
            Map<String, Album> albumNameMap = new HashMap<>();
            Map<String, Tag> tagNameMap = new HashMap<>();

            List<Album> existingAlbums = albumRepository.findByUserIdOrderByDisplayOrderAscCreateTimeAsc(userId);
            for (Album a : existingAlbums) {
                albumNameMap.put(a.getName(), a);
            }

            List<Tag> existingTags = tagRepository.findByUserIdOrderByReferenceCountDescCreateTimeDesc(userId);
            for (Tag t : existingTags) {
                tagNameMap.put(t.getName(), t);
            }

            List<Picture> existingPictures = pictureRepository.findByUserIdOrderByCreateTimeDesc(userId);
            for (Picture p : existingPictures) {
                if (!Boolean.TRUE.equals(p.getDeleted())) {
                    picNameMap.put(p.getName(), p);
                }
            }

            if (metadata.getAlbums() != null) {
                for (BackupMetadata.BackupAlbum ba : metadata.getAlbums()) {
                    Album album = albumNameMap.get(ba.getName());
                    if (album == null) {
                        AlbumCreateRequest request = new AlbumCreateRequest();
                        request.setName(ba.getName());
                        request.setDescription(ba.getDescription());
                        request.setIsPublic(true);
                        request.setDisplayOrder(ba.getDisplayOrder());
                        try {
                            AlbumDTO albumDTO = albumService.createAlbum(request, userId);
                            album = albumRepository.findById(albumDTO.getId()).orElse(null);
                            if (album != null) {
                                albumNameMap.put(album.getName(), album);
                                restoredAlbums++;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

            if (metadata.getTags() != null) {
                for (BackupMetadata.BackupTag bt : metadata.getTags()) {
                    Tag tag = tagNameMap.get(bt.getName());
                    if (tag == null) {
                        tag = tagService.getOrCreateTag(bt.getName(), userId);
                        if (tag != null) {
                            tagNameMap.put(tag.getName(), tag);
                            restoredTags++;
                        }
                    }
                }
            }

            if (metadata.getPictures() != null) {
                File[] picFiles = picturesDir.toFile().listFiles();
                Map<String, File> picFileMap = new HashMap<>();
                if (picFiles != null) {
                    for (File pf : picFiles) {
                        if (pf.isFile()) {
                            picFileMap.put(pf.getName(), pf);
                        }
                    }
                }

                for (BackupMetadata.BackupPicture bp : metadata.getPictures()) {
                    String picName = bp.getName();
                    Picture existingPic = picNameMap.get(picName);

                    File sourceFile = findPictureFile(picFileMap, picName);
                    if (sourceFile == null) {
                        skippedPictures++;
                        continue;
                    }

                    if (existingPic != null) {
                        if ("skip".equals(strategy)) {
                            skippedPictures++;
                            continue;
                        } else if ("keep_both".equals(strategy)) {
                            int counter = 1;
                            String newName;
                            do {
                                int dotIdx = picName.lastIndexOf('.');
                                if (dotIdx > 0) {
                                    newName = picName.substring(0, dotIdx) + "_" + counter + picName.substring(dotIdx);
                                } else {
                                    newName = picName + "_" + counter;
                                }
                                counter++;
                            } while (picNameMap.containsKey(newName));

                            Picture newPic = importPicture(sourceFile, newName, bp, userId, albumNameMap, tagNameMap);
                            if (newPic != null) {
                                picNameMap.put(newPic.getName(), newPic);
                                restoredPictures++;
                            }
                        } else {
                            overwritePicture(existingPic, sourceFile, bp, userId, albumNameMap, tagNameMap);
                            restoredPictures++;
                        }
                    } else {
                        Picture newPic = importPicture(sourceFile, picName, bp, userId, albumNameMap, tagNameMap);
                        if (newPic != null) {
                            picNameMap.put(newPic.getName(), newPic);
                            restoredPictures++;
                        }
                    }
                }
            }

            if (metadata.getAlbums() != null) {
                for (BackupMetadata.BackupAlbum ba : metadata.getAlbums()) {
                    Album album = albumNameMap.get(ba.getName());
                    if (album != null && ba.getPictureNames() != null) {
                        for (String picName : ba.getPictureNames()) {
                            Picture pic = picNameMap.get(picName);
                            if (pic != null) {
                                if (!pic.getAlbums().contains(album)) {
                                    pic.getAlbums().add(album);
                                    pictureRepository.save(pic);
                                }
                            }
                        }
                    }
                }
            }

            result.setRestoredPictures(restoredPictures);
            result.setRestoredAlbums(restoredAlbums);
            result.setRestoredTags(restoredTags);
            result.setSkippedPictures(skippedPictures);
            result.setSuccess(true);

            return result;
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    private File findPictureFile(Map<String, File> picFileMap, String picName) {
        if (picFileMap.containsKey(picName)) {
            return picFileMap.get(picName);
        }
        String baseName = picName;
        int dotIdx = picName.lastIndexOf('.');
        if (dotIdx > 0) {
            baseName = picName.substring(0, dotIdx);
        }
        for (Map.Entry<String, File> entry : picFileMap.entrySet()) {
            String name = entry.getKey();
            int d = name.lastIndexOf('.');
            String entryBase = d > 0 ? name.substring(0, d) : name;
            if (entryBase.equals(baseName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Picture importPicture(File sourceFile, String picName, BackupMetadata.BackupPicture bp, Long userId,
                                   Map<String, Album> albumNameMap, Map<String, Tag> tagNameMap) {
        try {
            String suffix = "";
            int dotIdx = sourceFile.getName().lastIndexOf('.');
            if (dotIdx > 0) {
                suffix = sourceFile.getName().substring(dotIdx);
            }

            String newFileName = UUID.randomUUID().toString() + suffix;
            Path destPath = Paths.get(uploadPath + newFileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            Picture picture = new Picture();
            picture.setUserId(userId);
            picture.setName(picName);
            picture.setUrl("/images/" + newFileName);
            picture.setSize(sourceFile.length());
            picture.setIsPublic(bp.getIsPublic());

            Set<Album> albums = new HashSet<>();
            if (bp.getAlbums() != null) {
                for (String albumName : bp.getAlbums()) {
                    Album album = albumNameMap.get(albumName);
                    if (album != null) {
                        albums.add(album);
                    }
                }
            }
            if (albums.isEmpty()) {
                albums.add(albumService.getDefaultAlbum(userId));
            }
            picture.setAlbums(albums);

            Set<Tag> tags = new HashSet<>();
            if (bp.getTags() != null) {
                for (String tagName : bp.getTags()) {
                    Tag tag = tagNameMap.get(tagName);
                    if (tag == null) {
                        tag = tagService.getOrCreateTag(tagName, userId);
                        if (tag != null) {
                            tagNameMap.put(tag.getName(), tag);
                        }
                    }
                    if (tag != null) {
                        tags.add(tag);
                        tagService.incrementReferenceCount(tag);
                    }
                }
            }
            picture.setTags(tags);

            return pictureRepository.save(picture);
        } catch (Exception e) {
            return null;
        }
    }

    private void overwritePicture(Picture picture, File sourceFile, BackupMetadata.BackupPicture bp, Long userId,
                                   Map<String, Album> albumNameMap, Map<String, Tag> tagNameMap) {
        try {
            String oldFileName = picture.getUrl().replace("/images/", "");
            File oldFile = new File(uploadPath + oldFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            String suffix = "";
            int dotIdx = sourceFile.getName().lastIndexOf('.');
            if (dotIdx > 0) {
                suffix = sourceFile.getName().substring(dotIdx);
            }

            String newFileName = UUID.randomUUID().toString() + suffix;
            Path destPath = Paths.get(uploadPath + newFileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            picture.setUrl("/images/" + newFileName);
            picture.setSize(sourceFile.length());
            if (bp.getIsPublic() != null) {
                picture.setIsPublic(bp.getIsPublic());
            }

            for (Tag oldTag : picture.getTags()) {
                tagService.decrementReferenceCount(oldTag);
            }

            Set<Tag> newTags = new HashSet<>();
            if (bp.getTags() != null) {
                for (String tagName : bp.getTags()) {
                    Tag tag = tagNameMap.get(tagName);
                    if (tag == null) {
                        tag = tagService.getOrCreateTag(tagName, userId);
                        if (tag != null) {
                            tagNameMap.put(tag.getName(), tag);
                        }
                    }
                    if (tag != null) {
                        newTags.add(tag);
                        tagService.incrementReferenceCount(tag);
                    }
                }
            }
            picture.setTags(newTags);

            pictureRepository.save(picture);
        } catch (Exception e) {
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }

    private BackupDTO toDTO(Backup backup) {
        BackupDTO dto = new BackupDTO();
        dto.setId(backup.getId());
        dto.setFileName(backup.getFileName());
        dto.setFileSize(backup.getFileSize());
        dto.setPictureCount(backup.getPictureCount());
        dto.setAlbumCount(backup.getAlbumCount());
        dto.setTagCount(backup.getTagCount());
        dto.setStatus(backup.getStatus());
        dto.setErrorMessage(backup.getErrorMessage());
        dto.setCreateTime(backup.getCreateTime());
        dto.setCompleteTime(backup.getCompleteTime());
        return dto;
    }
}
