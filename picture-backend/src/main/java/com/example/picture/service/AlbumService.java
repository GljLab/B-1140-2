package com.example.picture.service;

import com.example.picture.context.UserContext;
import com.example.picture.dto.AlbumCreateRequest;
import com.example.picture.dto.AlbumDTO;
import com.example.picture.dto.AlbumUpdateRequest;
import com.example.picture.entity.Album;
import com.example.picture.entity.Picture;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
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
        Album album = albumRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));
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
        if (request.getIsPublic() != null) {
            album.setIsPublic(request.getIsPublic());
        }
        if (request.getDisplayOrder() != null) {
            album.setDisplayOrder(request.getDisplayOrder());
        }
        Album saved = albumRepository.save(album);
        return toDTO(saved, userId);
    }

    @Transactional
    public void deleteAlbum(Long id, boolean deletePictures, Long userId) {
        Album album = albumRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));
        if (album.getIsDefault()) {
            throw new RuntimeException("默认专辑不可删除");
        }
        List<Picture> pictures = pictureRepository.findByAlbumIdAndUserId(id, userId);
        if (deletePictures) {
            for (Picture pic : pictures) {
                String fileName = pic.getUrl().replace("/images/", "");
                java.io.File file = new java.io.File("/app/images/" + fileName);
                if (file.exists()) {
                    file.delete();
                }
                pic.getTags().clear();
                pictureRepository.delete(pic);
            }
        } else {
            Album defaultAlbum = getDefaultAlbum(userId);
            for (Picture pic : pictures) {
                pic.getAlbums().remove(album);
                pic.getAlbums().add(defaultAlbum);
                pictureRepository.save(pic);
            }
        }
        albumRepository.delete(album);
    }

    public List<AlbumDTO> listAlbums(Long userId) {
        List<Album> albums = albumRepository.findByUserIdOrderByDisplayOrderAscCreateTimeAsc(userId);
        return albums.stream().map(a -> toDTO(a, userId)).collect(Collectors.toList());
    }

    public AlbumDTO getAlbum(Long id, Long userId) {
        Album album = albumRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));
        return toDTO(album, userId);
    }

    public AlbumDTO toDTO(Album album, Long userId) {
        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        dto.setName(album.getName());
        dto.setDescription(album.getDescription());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setCoverPictureId(album.getCoverPictureId());
        dto.setIsPublic(album.getIsPublic());
        dto.setIsDefault(album.getIsDefault());
        dto.setDisplayOrder(album.getDisplayOrder());
        dto.setCreateTime(album.getCreateTime());
        dto.setUpdateTime(album.getUpdateTime());
        Long count = pictureRepository.countByAlbumIdAndUserId(album.getId(), userId);
        dto.setPictureCount(count != null ? count.intValue() : 0);
        Long size = pictureRepository.sumSizesByAlbumIdAndUserId(album.getId(), userId);
        dto.setTotalSize(size != null ? size : 0L);
        dto.setLastUploadTime(pictureRepository.findLastUploadTimeByAlbumIdAndUserId(album.getId(), userId));
        return dto;
    }

    @Transactional
    public void addPictureToAlbum(Long albumId, Long pictureId, Long userId) {
        Album album = albumRepository.findByIdAndUserId(albumId, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        picture.getAlbums().add(album);
        pictureRepository.save(picture);
    }

    @Transactional
    public void removePictureFromAlbum(Long albumId, Long pictureId, Long userId) {
        Album album = albumRepository.findByIdAndUserId(albumId, userId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在");
        }
        picture.getAlbums().remove(album);
        if (picture.getAlbums().isEmpty()) {
            Album defaultAlbum = getDefaultAlbum(userId);
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
        List<Album> albums = albumRepository.findByUserIdAndNameContainingIgnoreCase(userId, keyword.trim());
        return albums.stream().map(a -> toDTO(a, userId)).collect(Collectors.toList());
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
