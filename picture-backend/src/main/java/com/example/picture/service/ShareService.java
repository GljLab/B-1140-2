package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.Album;
import com.example.picture.entity.Picture;
import com.example.picture.entity.Share;
import com.example.picture.entity.User;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.repository.ShareRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShareService {

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private UserService userService;

    public AlbumDTO albumToDTO(Album album) {
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
        if (album.getPictures() != null) {
            dto.setPictureCount(album.getPictures().size());
        }
        return dto;
    }

    @Transactional
    public ShareDTO createShare(ShareCreateRequest request, Long userId) {
        String shareType = request.getShareType();
        if (!"picture".equals(shareType) && !"album".equals(shareType)) {
            throw new RuntimeException("无效的分享类型");
        }

        Share share = new Share();
        share.setUserId(userId);
        share.setShareType(shareType);
        share.setExpireDays(request.getExpireDays() != null ? request.getExpireDays() : 0);

        if ("picture".equals(shareType)) {
            Picture picture = pictureRepository.findById(request.getPictureId()).orElse(null);
            if (picture == null || !picture.getUserId().equals(userId)) {
                throw new RuntimeException("图片不存在或无权限");
            }
            share.setPictureId(picture.getId());
            share.setTitle(picture.getName());
            share.setCoverUrl(picture.getUrl());
        } else {
            Album album = albumRepository.findById(request.getAlbumId()).orElse(null);
            if (album == null || !album.getUserId().equals(userId)) {
                throw new RuntimeException("专辑不存在或无权限");
            }
            share.setAlbumId(album.getId());
            share.setTitle(album.getName());
            share.setCoverUrl(album.getCoverUrl());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            share.setPassword(request.getPassword().trim());
        }

        String token = generateToken();
        share.setShareToken(token);
        share.setViewCount(0);

        Share saved = shareRepository.save(share);
        return toDTO(saved, true);
    }

    public ShareDTO getShareInfo(String token) {
        Share share = shareRepository.findByShareToken(token).orElse(null);
        if (share == null) {
            throw new RuntimeException("分享链接不存在");
        }
        return toDTO(share, false);
    }

    @Transactional
    public ShareDTO accessShare(String token, String password) {
        Share share = shareRepository.findByShareToken(token).orElse(null);
        if (share == null) {
            throw new RuntimeException("分享链接不存在");
        }

        if (share.isExpired()) {
            throw new RuntimeException("分享已过期");
        }

        if (share.getPassword() != null && !share.getPassword().isEmpty()) {
            if (password == null || !password.equals(share.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        }

        share.setViewCount(share.getViewCount() + 1);
        shareRepository.save(share);

        return toDTOWithContent(share);
    }

    public List<ShareDTO> getMyShares(Long userId) {
        List<Share> shares = shareRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return shares.stream().map(s -> toDTO(s, true)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteShare(Long id, Long userId) {
        Share share = shareRepository.findByIdAndUserId(id, userId).orElse(null);
        if (share == null) {
            throw new RuntimeException("分享记录不存在");
        }
        shareRepository.delete(share);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private ShareDTO toDTO(Share share, boolean includeSensitive) {
        ShareDTO dto = new ShareDTO();
        dto.setId(share.getId());
        dto.setShareType(share.getShareType());
        dto.setPictureId(share.getPictureId());
        dto.setAlbumId(share.getAlbumId());
        dto.setTitle(share.getTitle());
        dto.setCoverUrl(share.getCoverUrl());
        dto.setShareToken(share.getShareToken());
        dto.setHasPassword(share.getPassword() != null && !share.getPassword().isEmpty());
        dto.setExpireDays(share.getExpireDays());
        dto.setExpireTime(share.getExpireTime());
        dto.setViewCount(share.getViewCount());
        dto.setCreateTime(share.getCreateTime());
        dto.setExpired(share.isExpired());
        dto.setShareUrl("#/share/" + share.getShareToken());

        User user = userRepository.findById(share.getUserId()).orElse(null);
        if (user != null) {
            dto.setCreator(userService.toDTO(user));
        }

        return dto;
    }

    private ShareDTO toDTOWithContent(Share share) {
        ShareDTO dto = toDTO(share, false);

        if ("picture".equals(share.getShareType()) && share.getPictureId() != null) {
            Picture picture = pictureRepository.findById(share.getPictureId()).orElse(null);
            if (picture != null) {
                dto.setPicture(pictureService.toDTO(picture));
            }
        } else if ("album".equals(share.getShareType()) && share.getAlbumId() != null) {
            Album album = albumRepository.findById(share.getAlbumId()).orElse(null);
            if (album != null) {
                AlbumDTO albumDTO = albumToDTO(album);
                List<Picture> pictures = pictureRepository.findByAlbumId(album.getId());
                Long count = pictureRepository.countByAlbumId(album.getId());
                albumDTO.setPictureCount(count != null ? count.intValue() : 0);
                if (pictures != null && !pictures.isEmpty()) {
                    List<PictureDTO> pictureDTOs = pictures.stream()
                            .map(pictureService::toDTO)
                            .collect(Collectors.toList());
                    albumDTO.setPictures(pictureDTOs);
                }
                dto.setAlbum(albumDTO);
            }
        }

        return dto;
    }
}
