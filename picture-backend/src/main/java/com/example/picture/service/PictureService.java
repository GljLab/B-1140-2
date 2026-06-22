package com.example.picture.service;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.entity.Album;
import com.example.picture.entity.Picture;
import com.example.picture.entity.Tag;
import com.example.picture.entity.User;
import com.example.picture.entity.WatermarkConfig;
import com.example.picture.entity.WatermarkTemplate;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.TagRepository;
import com.example.picture.repository.UserRepository;
import com.example.picture.util.ImageHashUtil;
import com.example.picture.util.WatermarkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InteractionService interactionService;

    @Autowired
    private com.example.picture.repository.AlbumCollaboratorRepository albumCollaboratorRepository;

    @Autowired
    private WatermarkService watermarkService;

    @Value("${upload.path:/app/images/}")
    private String uploadPath;

    @Value("${recycle.retention-days:30}")
    private int retentionDays = 30;

    @PostConstruct
    public void init() {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Transactional
    public PictureDTO upload(MultipartFile file, List<Long> albumIds, List<String> tagNames, Long userId) throws IOException {
        return upload(file, albumIds, tagNames, userId, false, null);
    }

    @Transactional
    public PictureDTO upload(MultipartFile file, List<Long> albumIds, List<String> tagNames, Long userId, Boolean addWatermark, Long templateId) throws IOException {
        albumService.ensureDefaultAlbum(userId);

        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String originalFileName = UUID.randomUUID().toString() + suffix;
        Path originalPath = Paths.get(uploadPath + originalFileName);
        Files.write(originalPath, file.getBytes());

        String finalFileName = originalFileName;
        String finalUrl = "/images/" + originalFileName;
        boolean hasWatermark = false;

        if (Boolean.TRUE.equals(addWatermark)) {
            WatermarkConfig config = null;
            if (templateId != null) {
                WatermarkTemplate template = watermarkService.getTemplateEntity(templateId, userId);
                if (template != null) {
                    config = watermarkService.templateToConfig(template, userId);
                }
            }
            if (config == null) {
                config = watermarkService.getConfigEntity(userId);
            }
            if (config != null) {
                String watermarkedFileName = UUID.randomUUID().toString() + suffix;
                File watermarkedFile = new File(uploadPath + watermarkedFileName);
                try {
                    WatermarkUtil.addWatermark(originalPath.toFile(), watermarkedFile, config, uploadPath);
                    finalFileName = watermarkedFileName;
                    finalUrl = "/images/" + watermarkedFileName;
                    hasWatermark = true;
                } catch (Exception e) {
                    watermarkedFile.delete();
                }
            }
        }

        Picture picture = new Picture();
        picture.setUserId(userId);
        picture.setName(originalName);
        picture.setUrl(finalUrl);
        if (hasWatermark) {
            picture.setOriginalUrl("/images/" + originalFileName);
        }
        picture.setHasWatermark(hasWatermark);
        picture.setSize(new File(uploadPath + finalFileName).length());

        try {
            File finalImageFile = new File(uploadPath + finalFileName);
            picture.setMd5Hash(ImageHashUtil.computeMD5(finalImageFile));
            BufferedImage bimg = ImageIO.read(finalImageFile);
            if (bimg != null) {
                picture.setPerceptualHash(ImageHashUtil.computePerceptualHash(bimg));
                picture.setWidth(bimg.getWidth());
                picture.setHeight(bimg.getHeight());
            }
        } catch (Exception ignored) {
        }

        Set<Album> albums = new HashSet<>();
        if (albumIds != null && !albumIds.isEmpty()) {
            for (Long aid : albumIds) {
                albumRepository.findByIdAndUserId(aid, userId).ifPresent(albums::add);
                if (albums.stream().noneMatch(a -> a.getId().equals(aid))) {
                    albumRepository.findById(aid).ifPresent(album -> {
                        if (albumCollaboratorRepository.existsByAlbumIdAndUserId(album.getId(), userId)) {
                            albums.add(album);
                        }
                    });
                }
            }
        }
        if (albums.isEmpty()) {
            albums.add(albumService.getDefaultAlbum(userId));
        }
        picture.setAlbums(albums);

        Set<Tag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                if (tagName == null) continue;
                String[] parts = tagName.split("[,，]");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        Tag tag = tagService.getOrCreateTag(trimmed, userId);
                        if (tag != null) {
                            tags.add(tag);
                        }
                    }
                }
            }
        }
        for (Tag tag : tags) {
            tagService.incrementReferenceCount(tag);
        }
        picture.setTags(tags);

        Picture saved = pictureRepository.save(picture);
        return toDTO(saved, userId);
    }

    public List<PictureDTO> listPictures(Long albumId, Long tagId, String keyword, Long userId) {
        List<Picture> pictures;
        if (albumId != null) {
            pictures = pictureRepository.findNonPrivateByAlbumIdAndUserId(albumId, userId);
        } else if (tagId != null) {
            pictures = pictureRepository.findNonPrivateByTagIdAndUserId(tagId, userId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            pictures = pictureRepository.findNonPrivateByNameContainingKeywordAndUserId(keyword.trim(), userId);
        } else {
            pictures = pictureRepository.findNonPrivateByUserIdOrderByCreateTimeDesc(userId);
        }
        return pictures.stream().map(p -> toDTO(p, userId)).collect(Collectors.toList());
    }

    public List<PictureDTO> listAlbumPictures(Long albumId, Long userId) {
        com.example.picture.entity.Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) {
            throw new RuntimeException("专辑不存在");
        }
        boolean isOwner = album.getUserId().equals(userId);
        boolean isCollaborator = albumCollaboratorRepository.existsByAlbumIdAndUserId(albumId, userId);
        if (!isOwner && !isCollaborator) {
            throw new RuntimeException("无权限访问此专辑");
        }
        List<Picture> pictures = pictureRepository.findNonPrivateByAlbumIdAndUserId(albumId, userId);
        return pictures.stream().map(p -> toDTO(p, userId)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PictureDTO getPicture(Long id, Long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (Boolean.TRUE.equals(picture.getIsPrivate()) && picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        boolean isOwner = picture.getUserId().equals(userId);
        boolean isPublic = Boolean.TRUE.equals(picture.getIsPublic());
        boolean canAccessViaAlbum = false;
        if (!isOwner && !isPublic) {
            for (com.example.picture.entity.Album album : picture.getAlbums()) {
                if (album.getUserId().equals(userId) ||
                    albumCollaboratorRepository.existsByAlbumIdAndUserId(album.getId(), userId)) {
                    canAccessViaAlbum = true;
                    break;
                }
            }
        }
        if (!isOwner && !isPublic && !canAccessViaAlbum) {
            throw new RuntimeException("图片不存在");
        }
        return toDTO(picture, userId);
    }

    @Transactional
    public PictureDTO updatePicture(Long id, PictureUpdateRequest request, Long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        if (Boolean.TRUE.equals(picture.getIsPrivate())) {
            throw new RuntimeException("图片不存在");
        }

        if (request.getIsPublic() != null) {
            picture.setIsPublic(request.getIsPublic());
        }

        if (request.getAlbumIds() != null) {
            Set<Album> newAlbums = new HashSet<>();
            for (Long aid : request.getAlbumIds()) {
                albumRepository.findByIdAndUserId(aid, userId).ifPresent(newAlbums::add);
                if (newAlbums.stream().noneMatch(a -> a.getId().equals(aid))) {
                    albumRepository.findById(aid).ifPresent(album -> {
                        if (albumCollaboratorRepository.existsByAlbumIdAndUserId(album.getId(), userId)) {
                            newAlbums.add(album);
                        }
                    });
                }
            }
            if (newAlbums.isEmpty()) {
                newAlbums.add(albumService.getDefaultAlbum(userId));
            }
            picture.setAlbums(newAlbums);
        }

        if (request.getTagNames() != null) {
            Set<Tag> oldTags = new HashSet<>(picture.getTags());
            Set<String> newTagNames = new HashSet<>();
            for (String tn : request.getTagNames()) {
                if (tn == null) continue;
                String[] parts = tn.split("[,，]");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) newTagNames.add(trimmed);
                }
            }
            Set<Tag> newTags = new HashSet<>();
            for (String name : newTagNames) {
                Tag tag = tagService.getOrCreateTag(name, userId);
                if (tag != null) {
                    newTags.add(tag);
                    if (!oldTags.contains(tag)) {
                        tagService.incrementReferenceCount(tag);
                    }
                }
            }
            for (Tag oldTag : oldTags) {
                if (!newTags.contains(oldTag)) {
                    tagService.decrementReferenceCount(oldTag);
                }
            }
            picture.setTags(newTags);
        }

        Picture saved = pictureRepository.save(picture);
        return toDTO(saved, userId);
    }

    @Transactional
    public void deletePicture(Long id, Long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            return;
        }
        if (Boolean.TRUE.equals(picture.getDeleted())) {
            return;
        }
        if (Boolean.TRUE.equals(picture.getIsPrivate())) {
            return;
        }
        for (Tag tag : picture.getTags()) {
            tagService.decrementReferenceCount(tag);
        }
        picture.setDeleted(true);
        picture.setDeleteTime(new Date());
        pictureRepository.save(picture);
    }

    @Transactional
    public void permanentDeletePicture(Long id, Long userId) {
        Picture picture = pictureRepository.findDeletedByIdAndUserId(id, userId).orElse(null);
        if (picture == null) {
            return;
        }
        String fileName = picture.getUrl().replace("/images/", "");
        File file = new File(uploadPath + fileName);
        if (file.exists()) {
            file.delete();
        }
        picture.getAlbums().clear();
        picture.getTags().clear();
        pictureRepository.delete(picture);
    }

    @Transactional
    public void restorePicture(Long id, Long userId) {
        Picture picture = pictureRepository.findDeletedByIdAndUserId(id, userId).orElse(null);
        if (picture == null) {
            return;
        }
        for (Tag tag : picture.getTags()) {
            tagService.incrementReferenceCount(tag);
        }
        picture.setDeleted(false);
        picture.setDeleteTime(null);
        pictureRepository.save(picture);
    }

    public List<PictureDTO> listRecycleBin(Long userId) {
        List<Picture> pictures = pictureRepository.findDeletedByUserId(userId);
        return pictures.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void batchRestore(BatchDeleteRequest request, Long userId) {
        if (request.getPictureIds() == null) return;
        for (Long pid : request.getPictureIds()) {
            restorePicture(pid, userId);
        }
    }

    @Transactional
    public void batchPermanentDelete(BatchDeleteRequest request, Long userId) {
        if (request.getPictureIds() == null) return;
        for (Long pid : request.getPictureIds()) {
            permanentDeletePicture(pid, userId);
        }
    }

    @Transactional
    public void clearRecycleBin(Long userId) {
        List<Picture> pictures = pictureRepository.findDeletedByUserId(userId);
        for (Picture picture : pictures) {
            String fileName = picture.getUrl().replace("/images/", "");
            File file = new File(uploadPath + fileName);
            if (file.exists()) {
                file.delete();
            }
            picture.getAlbums().clear();
            picture.getTags().clear();
            pictureRepository.delete(picture);
        }
    }

    public long countRecycleBin(Long userId) {
        return pictureRepository.countDeletedByUserId(userId);
    }

    @Transactional
    public void cleanExpiredPictures() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
        Date expireTime = calendar.getTime();
        List<Picture> expiredPictures = pictureRepository.findExpiredDeletedPictures(expireTime);
        for (Picture picture : expiredPictures) {
            String fileName = picture.getUrl().replace("/images/", "");
            File file = new File(uploadPath + fileName);
            if (file.exists()) {
                file.delete();
            }
            picture.getAlbums().clear();
            picture.getTags().clear();
            pictureRepository.delete(picture);
        }
    }

    @Transactional
    public void batchAddTags(BatchTagRequest request, Long userId) {
        if (request.getPictureIds() == null || request.getTagNames() == null) return;
        List<Tag> tagsToAdd = new ArrayList<>();
        Set<String> processed = new HashSet<>();
        for (String tn : request.getTagNames()) {
            if (tn == null) continue;
            String[] parts = tn.split("[,，]");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && !processed.contains(trimmed)) {
                    processed.add(trimmed);
                    Tag tag = tagService.getOrCreateTag(trimmed, userId);
                    if (tag != null) tagsToAdd.add(tag);
                }
            }
        }
        for (Long pid : request.getPictureIds()) {
            Picture picture = pictureRepository.findById(pid).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId)) continue;
            for (Tag tag : tagsToAdd) {
                if (!picture.getTags().contains(tag)) {
                    picture.getTags().add(tag);
                    tagService.incrementReferenceCount(tag);
                }
            }
            pictureRepository.save(picture);
        }
    }

    @Transactional
    public void batchAddToAlbum(BatchAlbumRequest request, Long userId) {
        if (request.getPictureIds() == null || request.getAlbumId() == null) return;
        Album album = albumRepository.findByIdAndUserId(request.getAlbumId(), userId).orElse(null);
        if (album == null) {
            album = albumRepository.findById(request.getAlbumId()).orElse(null);
            if (album == null || !albumCollaboratorRepository.existsByAlbumIdAndUserId(album.getId(), userId)) {
                return;
            }
        }
        for (Long pid : request.getPictureIds()) {
            Picture picture = pictureRepository.findById(pid).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId)) continue;
            if (!picture.getAlbums().contains(album)) {
                picture.getAlbums().add(album);
                pictureRepository.save(picture);
            }
        }
    }

    @Transactional
    public void batchDelete(BatchDeleteRequest request, Long userId) {
        if (request.getPictureIds() == null) return;
        for (Long pid : request.getPictureIds()) {
            deletePicture(pid, userId);
        }
    }

    @Transactional
    public PictureDTO addWatermarkToPicture(Long id, Long userId, Long templateId) throws IOException {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        if (Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }

        WatermarkConfig config = null;
        if (templateId != null) {
            WatermarkTemplate template = watermarkService.getTemplateEntity(templateId, userId);
            if (template != null) {
                config = watermarkService.templateToConfig(template, userId);
            }
        }
        if (config == null) {
            config = watermarkService.getConfigEntity(userId);
        }
        if (config == null) {
            throw new RuntimeException("请先设置水印配置或选择水印模板");
        }

        String sourceUrl = picture.getOriginalUrl() != null ? picture.getOriginalUrl() : picture.getUrl();
        String sourceFileName = sourceUrl.replace("/images/", "");
        File sourceFile = new File(uploadPath + sourceFileName);
        if (!sourceFile.exists()) {
            throw new RuntimeException("原图文件不存在");
        }

        String originalName = picture.getName();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String watermarkedFileName = UUID.randomUUID().toString() + suffix;
        File watermarkedFile = new File(uploadPath + watermarkedFileName);

        WatermarkUtil.addWatermark(sourceFile, watermarkedFile, config, uploadPath);

        if (picture.getOriginalUrl() == null) {
            picture.setOriginalUrl(picture.getUrl());
        }
        picture.setUrl("/images/" + watermarkedFileName);
        picture.setHasWatermark(true);
        picture.setSize(watermarkedFile.length());

        Picture saved = pictureRepository.save(picture);
        return toDTO(saved, userId);
    }

    @Transactional
    public PictureDTO removeWatermarkFromPicture(Long id, Long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        if (Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!Boolean.TRUE.equals(picture.getHasWatermark()) || picture.getOriginalUrl() == null) {
            throw new RuntimeException("该图片没有水印或原图已被覆盖，无法恢复");
        }

        String originalFileName = picture.getOriginalUrl().replace("/images/", "");
        File originalFile = new File(uploadPath + originalFileName);
        if (!originalFile.exists()) {
            throw new RuntimeException("原图文件已不存在，无法恢复");
        }

        picture.setUrl(picture.getOriginalUrl());
        picture.setOriginalUrl(null);
        picture.setHasWatermark(false);
        picture.setSize(originalFile.length());

        Picture saved = pictureRepository.save(picture);
        return toDTO(saved, userId);
    }

    @Transactional
    public int batchAddWatermark(BatchWatermarkRequest request, Long userId) {
        if (request.getPictureIds() == null || request.getPictureIds().isEmpty()) {
            return 0;
        }
        int successCount = 0;
        for (Long pid : request.getPictureIds()) {
            try {
                addWatermarkToPicture(pid, userId, request.getTemplateId());
                successCount++;
            } catch (Exception e) {
            }
        }
        return successCount;
    }

    public PictureDTO toDTO(Picture picture) {
        return toDTO(picture, null);
    }

    public PictureDTO toDTO(Picture picture, Long currentUserId) {
        PictureDTO dto = new PictureDTO();
        dto.setId(picture.getId());
        dto.setName(picture.getName());
        dto.setUrl(picture.getUrl());
        dto.setOriginalUrl(picture.getOriginalUrl());
        dto.setHasWatermark(Boolean.TRUE.equals(picture.getHasWatermark()));
        dto.setSize(picture.getSize());
        dto.setCreateTime(picture.getCreateTime());
        dto.setUpdateTime(picture.getUpdateTime());
        dto.setDeleted(picture.getDeleted());
        dto.setDeleteTime(picture.getDeleteTime());
        dto.setIsPublic(Boolean.TRUE.equals(picture.getIsPublic()));
        dto.setIsPrivate(Boolean.TRUE.equals(picture.getIsPrivate()));
        dto.setUserId(picture.getUserId());

        userRepository.findById(picture.getUserId()).ifPresent(user -> {
            dto.setAuthorNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });

        dto.setLikeCount(interactionService.getLikeCount(picture.getId()));
        dto.setCommentCount(interactionService.getCommentCount(picture.getId()));
        dto.setFavoriteCount(interactionService.getFavoriteCount(picture.getId()));
        dto.setIsLiked(interactionService.isLiked(picture.getId(), currentUserId));
        dto.setIsFavorited(interactionService.isFavorited(picture.getId(), currentUserId));
        dto.setWidth(picture.getWidth());
        dto.setHeight(picture.getHeight());
        dto.setShootTime(picture.getShootTime());
        dto.setDominantColor(picture.getDominantColor());
        dto.setLatitude(picture.getLatitude());
        dto.setLongitude(picture.getLongitude());
        dto.setHasLocation(Boolean.TRUE.equals(picture.getHasLocation()));

        if (Boolean.TRUE.equals(picture.getDeleted()) && picture.getDeleteTime() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(picture.getDeleteTime());
            cal.add(Calendar.DAY_OF_MONTH, retentionDays);
            long diff = cal.getTimeInMillis() - System.currentTimeMillis();
            int remainingDays = (int) Math.ceil((double) diff / (1000 * 60 * 60 * 24));
            dto.setRemainingDays(Math.max(remainingDays, 0));
        }
        List<AlbumSimpleDTO> albumDTOs = picture.getAlbums().stream()
                .sorted(Comparator.comparing(Album::getDisplayOrder).thenComparing(Album::getCreateTime))
                .map(this::albumToSimpleDTO)
                .collect(Collectors.toList());
        dto.setAlbums(albumDTOs);
        List<TagDTO> tagDTOs = picture.getTags().stream()
                .sorted(Comparator.comparing(Tag::getReferenceCount).reversed().thenComparing(Tag::getCreateTime))
                .map(tagService::toDTO)
                .collect(Collectors.toList());
        dto.setTags(tagDTOs);
        return dto;
    }

    private AlbumSimpleDTO albumToSimpleDTO(Album album) {
        AlbumSimpleDTO dto = new AlbumSimpleDTO();
        dto.setId(album.getId());
        dto.setName(album.getName());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setIsDefault(album.getIsDefault());
        dto.setDisplayOrder(album.getDisplayOrder());
        dto.setCreateTime(album.getCreateTime());
        return dto;
    }

    private static class RenameUndoRecord {
        Long userId;
        java.util.Map<Long, String> oldNames;
        long expireTime;

        RenameUndoRecord(Long userId, java.util.Map<Long, String> oldNames, long expireTime) {
            this.userId = userId;
            this.oldNames = oldNames;
            this.expireTime = expireTime;
        }
    }

    private final java.util.Map<String, RenameUndoRecord> undoStore = new java.util.concurrent.ConcurrentHashMap<>();

    public List<RenamePreviewItem> previewRename(BatchRenameRequest request, Long userId) {
        if (request.getPictureIds() == null || request.getPictureIds().isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Picture> pictures = pictureRepository.findAllById(request.getPictureIds())
                .stream()
                .filter(p -> p.getUserId().equals(userId) && !Boolean.TRUE.equals(p.getDeleted()))
                .collect(java.util.stream.Collectors.toList());

        pictures.sort(Comparator.comparing(Picture::getCreateTime).reversed());

        int indexStart = request.getIndexStart() != null ? request.getIndexStart() : 1;
        int indexDigits = request.getIndexDigits() != null ? request.getIndexDigits() : 3;

        java.util.Set<String> newNameSet = new java.util.HashSet<>();
        java.util.Set<String> existingNames = pictureRepository.findNonPrivateByUserIdOrderByCreateTimeDesc(userId)
                .stream()
                .map(Picture::getName)
                .collect(java.util.stream.Collectors.toSet());

        List<RenamePreviewItem> results = new java.util.ArrayList<>();

        for (int i = 0; i < pictures.size(); i++) {
            Picture pic = pictures.get(i);
            RenamePreviewItem item = new RenamePreviewItem();
            item.setPictureId(pic.getId());
            item.setOldName(pic.getName());

            String newName = generateNewName(pic, request, indexStart + i, indexDigits);
            item.setNewName(newName);

            boolean hasConflict = false;
            String conflictReason = null;

            if (newNameSet.contains(newName)) {
                hasConflict = true;
                conflictReason = "批量重命名结果中存在重名";
            } else if (existingNames.contains(newName) && !newName.equals(pic.getName())) {
                hasConflict = true;
                conflictReason = "与其他图片名称冲突";
            }

            item.setHasConflict(hasConflict);
            item.setConflictReason(conflictReason);

            newNameSet.add(newName);
            results.add(item);
        }

        return results;
    }

    private String generateNewName(Picture picture, BatchRenameRequest request, int index, int indexDigits) {
        String originalName = picture.getName();
        String baseName = originalName;
        String extension = "";

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        String result = baseName;

        if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
            result = applyTemplate(picture, request.getTemplate(), request.getCustomText(), index, indexDigits);
        }

        if (request.getFindText() != null && !request.getFindText().isEmpty()) {
            if (Boolean.TRUE.equals(request.getUseRegex())) {
                try {
                    result = result.replaceAll(request.getFindText(), request.getReplaceText() != null ? request.getReplaceText() : "");
                } catch (Exception ignored) {
                }
            } else {
                String replaceText = request.getReplaceText() != null ? request.getReplaceText() : "";
                result = result.replace(request.getFindText(), replaceText);
            }
        }

        if (request.getCaseTransform() != null && !request.getCaseTransform().isEmpty()) {
            result = applyCaseTransform(result, request.getCaseTransform());
        }

        if (result.isEmpty()) {
            result = "untitled";
        }

        return result + extension;
    }

    private String applyTemplate(Picture picture, String template, String customText, int index, int indexDigits) {
        String result = template;

        result = result.replace("{index}", padNumber(index, indexDigits));

        if (result.contains("{date}")) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            String dateStr = sdf.format(picture.getCreateTime() != null ? picture.getCreateTime() : new Date());
            result = result.replace("{date}", dateStr);
        }

        if (result.contains("{album}")) {
            String albumName = "";
            if (picture.getAlbums() != null && !picture.getAlbums().isEmpty()) {
                albumName = picture.getAlbums().iterator().next().getName();
            }
            result = result.replace("{album}", safeName(albumName));
        }

        if (result.contains("{tag}")) {
            String tagName = "";
            if (picture.getTags() != null && !picture.getTags().isEmpty()) {
                tagName = picture.getTags().stream()
                        .max(Comparator.comparing(Tag::getReferenceCount))
                        .map(Tag::getName)
                        .orElse("");
            }
            result = result.replace("{tag}", safeName(tagName));
        }

        if (result.contains("{原名}")) {
            String originalName = picture.getName();
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex > 0) {
                originalName = originalName.substring(0, dotIndex);
            }
            result = result.replace("{原名}", originalName);
        }

        if (customText != null && result.contains("{custom}")) {
            result = result.replace("{custom}", safeName(customText));
        }

        return result;
    }

    private String safeName(String name) {
        if (name == null) return "";
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String padNumber(int number, int digits) {
        return String.format("%0" + digits + "d", number);
    }

    private String applyCaseTransform(String text, String transform) {
        if (text == null || text.isEmpty()) return text;

        switch (transform) {
            case "upper":
                return text.toUpperCase();
            case "lower":
                return text.toLowerCase();
            case "capitalize":
                if (text.length() <= 1) return text.toUpperCase();
                return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
            case "camel":
                return toCamelCase(text);
            default:
                return text;
        }
    }

    private String toCamelCase(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        boolean firstChar = true;

        for (char c : text.toCharArray()) {
            if (c == ' ' || c == '_' || c == '-') {
                nextUpper = true;
            } else {
                if (firstChar) {
                    result.append(Character.toLowerCase(c));
                    firstChar = false;
                } else if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    @Transactional
    public RenameResultDTO batchRename(BatchRenameRequest request, Long userId) {
        List<RenamePreviewItem> preview = previewRename(request, userId);

        java.util.Map<Long, String> oldNames = new java.util.HashMap<>();
        List<RenamePreviewItem> results = new java.util.ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (RenamePreviewItem item : preview) {
            if (Boolean.TRUE.equals(item.getHasConflict())) {
                failCount++;
                results.add(item);
                continue;
            }

            Picture picture = pictureRepository.findById(item.getPictureId()).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId)) {
                failCount++;
                item.setHasConflict(true);
                item.setConflictReason("图片不存在或无权限");
                results.add(item);
                continue;
            }

            oldNames.put(picture.getId(), picture.getName());
            picture.setName(item.getNewName());
            pictureRepository.save(picture);
            successCount++;
            results.add(item);
        }

        String undoToken = null;
        if (!oldNames.isEmpty()) {
            undoToken = java.util.UUID.randomUUID().toString();
            long expireTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
            undoStore.put(undoToken, new RenameUndoRecord(userId, oldNames, expireTime));
        }

        cleanExpiredUndoRecords();

        RenameResultDTO resultDTO = new RenameResultDTO();
        resultDTO.setSuccessCount(successCount);
        resultDTO.setFailCount(failCount);
        resultDTO.setResults(results);
        resultDTO.setUndoToken(undoToken);
        return resultDTO;
    }

    @Transactional
    public RenameResultDTO undoRename(String undoToken, Long userId) {
        RenameUndoRecord record = undoStore.get(undoToken);
        if (record == null) {
            throw new RuntimeException("撤销记录不存在或已过期");
        }
        if (!record.userId.equals(userId)) {
            throw new RuntimeException("无权限执行此操作");
        }
        if (record.expireTime < System.currentTimeMillis()) {
            undoStore.remove(undoToken);
            throw new RuntimeException("撤销记录已过期");
        }

        List<RenamePreviewItem> results = new java.util.ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (java.util.Map.Entry<Long, String> entry : record.oldNames.entrySet()) {
            RenamePreviewItem item = new RenamePreviewItem();
            item.setPictureId(entry.getKey());

            Picture picture = pictureRepository.findById(entry.getKey()).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId)) {
                failCount++;
                item.setOldName(entry.getValue());
                item.setHasConflict(true);
                item.setConflictReason("图片不存在或无权限");
                results.add(item);
                continue;
            }

            item.setOldName(picture.getName());
            item.setNewName(entry.getValue());
            item.setHasConflict(false);

            picture.setName(entry.getValue());
            pictureRepository.save(picture);
            successCount++;
            results.add(item);
        }

        undoStore.remove(undoToken);

        RenameResultDTO resultDTO = new RenameResultDTO();
        resultDTO.setSuccessCount(successCount);
        resultDTO.setFailCount(failCount);
        resultDTO.setResults(results);
        return resultDTO;
    }

    private void cleanExpiredUndoRecords() {
        long now = System.currentTimeMillis();
        undoStore.entrySet().removeIf(entry -> entry.getValue().expireTime < now);
    }
}
