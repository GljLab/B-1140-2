package com.example.picture.service;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.entity.Album;
import com.example.picture.entity.Picture;
import com.example.picture.entity.Tag;
import com.example.picture.entity.User;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.TagRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
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
        albumService.ensureDefaultAlbum(userId);

        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + suffix;
        Path path = Paths.get(uploadPath + fileName);
        Files.write(path, file.getBytes());

        Picture picture = new Picture();
        picture.setUserId(userId);
        picture.setName(originalName);
        picture.setUrl("/images/" + fileName);
        picture.setSize(file.getSize());

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
            pictures = pictureRepository.findByAlbumIdAndUserId(albumId, userId);
        } else if (tagId != null) {
            pictures = pictureRepository.findByTagIdAndUserId(tagId, userId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            pictures = pictureRepository.findByNameContainingKeywordAndUserId(keyword.trim(), userId);
        } else {
            pictures = pictureRepository.findByUserIdOrderByCreateTimeDesc(userId);
        }
        return pictures.stream().map(p -> toDTO(p, userId)).collect(Collectors.toList());
    }

    public List<PictureDTO> listAlbumPictures(Long albumId, Long userId) {
        List<Picture> pictures = pictureRepository.findByAlbumId(albumId);
        return pictures.stream().map(p -> toDTO(p, userId)).collect(Collectors.toList());
    }

    public PictureDTO getPicture(Long id, Long userId) {
        Picture picture = pictureRepository.findById(id).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId) && !Boolean.TRUE.equals(picture.getIsPublic())) {
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

    public PictureDTO toDTO(Picture picture) {
        return toDTO(picture, null);
    }

    public PictureDTO toDTO(Picture picture, Long currentUserId) {
        PictureDTO dto = new PictureDTO();
        dto.setId(picture.getId());
        dto.setName(picture.getName());
        dto.setUrl(picture.getUrl());
        dto.setSize(picture.getSize());
        dto.setCreateTime(picture.getCreateTime());
        dto.setUpdateTime(picture.getUpdateTime());
        dto.setDeleted(picture.getDeleted());
        dto.setDeleteTime(picture.getDeleteTime());
        dto.setIsPublic(Boolean.TRUE.equals(picture.getIsPublic()));
        dto.setUserId(picture.getUserId());

        userRepository.findById(picture.getUserId()).ifPresent(user -> {
            dto.setAuthorNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });

        dto.setLikeCount(interactionService.getLikeCount(picture.getId()));
        dto.setCommentCount(interactionService.getCommentCount(picture.getId()));
        dto.setFavoriteCount(interactionService.getFavoriteCount(picture.getId()));
        dto.setIsLiked(interactionService.isLiked(picture.getId(), currentUserId));
        dto.setIsFavorited(interactionService.isFavorited(picture.getId(), currentUserId));

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
}
