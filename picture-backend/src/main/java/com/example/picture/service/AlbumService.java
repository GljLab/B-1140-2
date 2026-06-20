package com.example.picture.service;

import com.example.picture.context.UserContext;
import com.example.picture.dto.AlbumCreateRequest;
import com.example.picture.dto.AlbumDTO;
import com.example.picture.dto.AlbumUpdateRequest;
import com.example.picture.entity.Album;
import com.example.picture.entity.AlbumCollaborator;
import com.example.picture.entity.Picture;
import com.example.picture.entity.User;
import com.example.picture.repository.AlbumCollaboratorRepository;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private AlbumCollaboratorRepository collaboratorRepository;

    @Autowired
    private UserRepository userRepository;

    public Album getDefaultAlbum(Long userId) {
        return albumRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new RuntimeException("默认专辑不存在"));
    }

    public AlbumDTO createAlbum(AlbumCreateRequest request, Long userId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("专辑名称不能为空");
        }
        if (request.getName().length() > 100) {
            throw new RuntimeException("专辑名称不能超过100字符");
        }
        String name = request.getName().trim();
        if (albumRepository.existsByUserIdAndName(userId, name)) {
            throw new RuntimeException("专辑名称已存在");
        }
        Album album = new Album();
        album.setUserId(userId);
        album.setName(name);
        album.setDescription(request.getDescription() != null && request.getDescription().length() <= 500 ?
                request.getDescription() : null);
        album.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        album.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        album.setIsDefault(false);
        Album saved = albumRepository.save(album);
        return toDTO(saved, userId);
    }

    public AlbumDTO updateAlbum(Long id, AlbumUpdateRequest request, Long userId) {
        Album album = albumRepository.findAccessibleAlbumById(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或无权限"));

        boolean isOwner = album.getUserId().equals(userId);
        boolean isCollaborator = collaboratorRepository.existsByAlbumIdAndUserId(id, userId);

        if (!isOwner && !isCollaborator) {
            throw new RuntimeException("无权限编辑此专辑");
        }

        if (album.getIsDefault() && request.getName() != null) {
            throw new RuntimeException("默认专辑名称不可修改");
        }
        if (request.getName() != null) {
            String name = request.getName().trim();
            if (name.isEmpty()) throw new RuntimeException("专辑名称不能为空");
            if (name.length() > 100) throw new RuntimeException("专辑名称不能超过100字符");
            if (!name.equals(album.getName()) && albumRepository.existsByUserIdAndName(userId, name)) {
                throw new RuntimeException("专辑名称已存在");
            }
            album.setName(name);
        }
        if (request.getDescription() != null) {
            if (request.getDescription().length() > 500) throw new RuntimeException("描述不能超过500字符");
            album.setDescription(request.getDescription());
        }
        if (request.getCoverUrl() != null) {
            album.setCoverUrl(request.getCoverUrl());
        }
        if (request.getCoverPictureId() != null) {
            album.setCoverPictureId(request.getCoverPictureId());
        }
        if (request.getIsPublic() != null && isOwner) {
            album.setIsPublic(request.getIsPublic());
        }
        if (request.getDisplayOrder() != null && isOwner) {
            album.setDisplayOrder(request.getDisplayOrder());
        }
        Album saved = albumRepository.save(album);
        return toDTO(saved, userId);
    }

    @Transactional
    public void deleteAlbum(Long id, boolean deletePictures, Long userId) {
        Album album = albumRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或无权限删除"));
        if (album.getIsDefault()) {
            throw new RuntimeException("默认专辑不可删除");
        }

        List<Picture> allPictures = pictureRepository.findByAlbumId(id);

        if (deletePictures) {
            for (Picture pic : allPictures) {
                if (pic.getUserId().equals(userId) && !Boolean.TRUE.equals(pic.getDeleted())) {
                    for (com.example.picture.entity.Tag tag : pic.getTags()) {
                        tagService.decrementReferenceCount(tag);
                    }
                    pic.setDeleted(true);
                    pic.setDeleteTime(new Date());
                    pictureRepository.save(pic);
                }
            }
        } else {
            Album defaultAlbum = getDefaultAlbum(userId);
            for (Picture pic : allPictures) {
                if (pic.getUserId().equals(userId)) {
                    pic.getAlbums().remove(album);
                    pic.getAlbums().add(defaultAlbum);
                    pictureRepository.save(pic);
                }
            }
        }
        albumRepository.delete(album);
    }

    public List<AlbumDTO> listAlbums(Long userId) {
        List<Album> myAlbums = albumRepository.findByUserIdOrderByDisplayOrderAscCreateTimeAsc(userId);
        List<Album> collaborationAlbums = albumRepository.findCollaborationAlbumsByUserId(userId);

        Set<Long> addedIds = new HashSet<>();
        List<AlbumDTO> result = new ArrayList<>();

        for (Album album : myAlbums) {
            if (!addedIds.contains(album.getId())) {
                result.add(toDTO(album, userId));
                addedIds.add(album.getId());
            }
        }

        for (Album album : collaborationAlbums) {
            if (!addedIds.contains(album.getId())) {
                result.add(toDTO(album, userId));
                addedIds.add(album.getId());
            }
        }

        return result;
    }

    public Map<String, List<AlbumDTO>> listAlbumsByCategory(Long userId) {
        List<Album> myAlbums = albumRepository.findByUserIdOrderByDisplayOrderAscCreateTimeAsc(userId);
        List<Album> collaborationAlbums = albumRepository.findCollaborationAlbumsByUserId(userId);

        Map<String, List<AlbumDTO>> result = new HashMap<>();
        result.put("created", myAlbums.stream().map(a -> toDTO(a, userId)).collect(Collectors.toList()));
        result.put("collaboration", collaborationAlbums.stream().map(a -> toDTO(a, userId)).collect(Collectors.toList()));
        return result;
    }

    public AlbumDTO getAlbum(Long id, Long userId) {
        Album album = albumRepository.findAccessibleAlbumById(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或无权限"));
        return toDTO(album, userId);
    }

    public AlbumDTO toDTO(Album album, Long userId) {
        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        dto.setUserId(album.getUserId());
        dto.setName(album.getName());
        dto.setDescription(album.getDescription());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setCoverPictureId(album.getCoverPictureId());
        dto.setIsPublic(album.getIsPublic());
        dto.setIsDefault(album.getIsDefault());
        dto.setDisplayOrder(album.getDisplayOrder());
        dto.setCreateTime(album.getCreateTime());
        dto.setUpdateTime(album.getUpdateTime());

        Long count = pictureRepository.countByAlbumId(album.getId());
        dto.setPictureCount(count != null ? count.intValue() : 0);

        Long size = pictureRepository.sumSizesByAlbumId(album.getId());
        dto.setTotalSize(size != null ? size : 0L);
        dto.setLastUploadTime(pictureRepository.findLastUploadTimeByAlbumId(album.getId()));

        boolean isOwner = album.getUserId().equals(userId);
        boolean isCollaborator = collaboratorRepository.existsByAlbumIdAndUserId(album.getId(), userId);

        dto.setIsCollaboration(!isOwner && isCollaborator);

        if (isOwner) {
            dto.setUserRole("OWNER");
        } else if (isCollaborator) {
            dto.setUserRole("COLLABORATOR");
        } else {
            dto.setUserRole("VIEWER");
        }

        User creator = userRepository.findById(album.getUserId()).orElse(null);
        if (creator != null) {
            dto.setCreatorNickname(creator.getNickname());
            dto.setCreatorUsername(creator.getUsername());
        }

        List<AlbumCollaborator> collaborators = collaboratorRepository.findByAlbumId(album.getId());
        dto.setCollaboratorCount(collaborators.size());

        return dto;
    }

    @Transactional
    public void addPictureToAlbum(Long albumId, Long pictureId, Long userId) {
        Album album = albumRepository.findAccessibleAlbumById(albumId, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或无权限"));

        boolean isOwner = album.getUserId().equals(userId);
        boolean isCollaborator = collaboratorRepository.existsByAlbumIdAndUserId(albumId, userId);

        if (!isOwner && !isCollaborator) {
            throw new RuntimeException("无权限添加图片到此专辑");
        }

        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        picture.getAlbums().add(album);
        pictureRepository.save(picture);
    }

    @Transactional
    public void removePictureFromAlbum(Long albumId, Long pictureId, Long userId) {
        Album album = albumRepository.findAccessibleAlbumById(albumId, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或无权限"));

        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("图片不存在");
        }

        boolean isOwner = album.getUserId().equals(userId);
        boolean isCollaborator = collaboratorRepository.existsByAlbumIdAndUserId(albumId, userId);
        boolean isPictureOwner = picture.getUserId().equals(userId);

        if (!isOwner && !isCollaborator && !isPictureOwner) {
            throw new RuntimeException("无权限移除此图片");
        }

        if (!isOwner && isCollaborator && !isPictureOwner) {
            throw new RuntimeException("只能移除自己上传的图片");
        }

        picture.getAlbums().remove(album);
        if (picture.getAlbums().isEmpty()) {
            Album defaultAlbum = getDefaultAlbum(picture.getUserId());
            picture.getAlbums().add(defaultAlbum);
        }
        pictureRepository.save(picture);
    }

    @Transactional
    public void reorderAlbums(Map<Long, Integer> orderMap, Long userId) {
        for (Map.Entry<Long, Integer> entry : orderMap.entrySet()) {
            albumRepository.findByIdAndUserId(entry.getKey(), userId).ifPresent(album -> {
                album.setDisplayOrder(entry.getValue());
                albumRepository.save(album);
            });
        }
    }

    public List<AlbumDTO> searchAlbums(String keyword, Long userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAlbums(userId);
        }
        List<Album> myAlbums = albumRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword.trim());
        List<Album> collaborationAlbums = albumRepository.findCollaborationAlbumsByUserId(userId);

        Set<Long> addedIds = new HashSet<>();
        List<AlbumDTO> result = new ArrayList<>();
        String kw = keyword.trim().toLowerCase();

        for (Album album : myAlbums) {
            if (!addedIds.contains(album.getId()) && album.getName().toLowerCase().contains(kw)) {
                result.add(toDTO(album, userId));
                addedIds.add(album.getId());
            }
        }

        for (Album album : collaborationAlbums) {
            if (!addedIds.contains(album.getId()) && album.getName().toLowerCase().contains(kw)) {
                result.add(toDTO(album, userId));
                addedIds.add(album.getId());
            }
        }

        return result;
    }

    public void ensureDefaultAlbum(Long userId) {
        Optional<Album> existing = albumRepository.findByUserIdAndIsDefaultTrue(userId);
        if (!existing.isPresent()) {
            Album defaultAlbum = new Album();
            defaultAlbum.setUserId(userId);
            defaultAlbum.setName("默认专辑");
            defaultAlbum.setDescription("系统默认专辑，存放未分类的图片");
            defaultAlbum.setIsDefault(true);
            defaultAlbum.setIsPublic(true);
            defaultAlbum.setDisplayOrder(0);
            albumRepository.save(defaultAlbum);
        }
    }
}
