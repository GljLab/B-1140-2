package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlideshowService {

    @Autowired
    private SlideshowRepository slideshowRepository;

    @Autowired
    private SlideshowItemRepository slideshowItemRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    public SlideshowDTO createSlideshow(SlideshowCreateRequest request, Long userId) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("幻灯片标题不能为空");
        }
        if (request.getTitle().length() > 100) {
            throw new RuntimeException("标题不能超过100字符");
        }

        Slideshow ss = new Slideshow();
        ss.setUserId(userId);
        ss.setTitle(request.getTitle().trim());
        ss.setDescription(request.getDescription());
        ss.setTransition(request.getTransition() != null ? request.getTransition() : "fade");
        ss.setInterval(request.getInterval() != null ? request.getInterval() : 5);
        ss.setLoop(request.getLoop() != null ? request.getLoop() : true);
        ss.setAutoStart(request.getAutoStart() != null ? request.getAutoStart() : true);
        ss.setBgMusicUrl(request.getBgMusicUrl());
        ss.setBgMusicType(request.getBgMusicType() != null ? request.getBgMusicType() : "custom");
        ss.setBgMusicVolume(request.getBgMusicVolume() != null ? request.getBgMusicVolume() : 0.5);
        ss.setBgMusicLoop(request.getBgMusicLoop() != null ? request.getBgMusicLoop() : true);
        ss.setEffect(request.getEffect() != null ? request.getEffect() : "none");
        ss.setEffectIntensity(request.getEffectIntensity() != null ? request.getEffectIntensity() : 0.5);
        ss.setTitlePageText(request.getTitlePageText());
        ss.setTextAnimation(request.getTextAnimation() != null ? request.getTextAnimation() : "fade");
        ss.setTextStyle(request.getTextStyle() != null ? request.getTextStyle() : "{}");
        ss.setTextPosition(request.getTextPosition() != null ? request.getTextPosition() : "bottom");
        ss.setStatus(0);
        ss.setIsPublic(false);
        ss.setAllowDownload(false);
        ss.setViewCount(0L);

        Slideshow saved = slideshowRepository.save(ss);

        if (request.getPictureIds() != null && !request.getPictureIds().isEmpty()) {
            addPicturesToSlideshow(saved.getId(), request.getPictureIds(), userId);
        } else if (request.getAlbumId() != null) {
            addAlbumToSlideshow(saved.getId(), request.getAlbumId(), userId);
        }

        return toDTO(saved, userId, true);
    }

    public SlideshowDTO updateSlideshow(Long id, SlideshowUpdateRequest request, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));

        if (request.getTitle() != null) {
            String title = request.getTitle().trim();
            if (title.isEmpty()) throw new RuntimeException("标题不能为空");
            ss.setTitle(title);
        }
        if (request.getDescription() != null) ss.setDescription(request.getDescription());
        if (request.getTransition() != null) ss.setTransition(request.getTransition());
        if (request.getInterval() != null) ss.setInterval(request.getInterval());
        if (request.getLoop() != null) ss.setLoop(request.getLoop());
        if (request.getAutoStart() != null) ss.setAutoStart(request.getAutoStart());
        if (request.getBgMusicUrl() != null) ss.setBgMusicUrl(request.getBgMusicUrl());
        if (request.getBgMusicType() != null) ss.setBgMusicType(request.getBgMusicType());
        if (request.getBgMusicVolume() != null) ss.setBgMusicVolume(request.getBgMusicVolume());
        if (request.getBgMusicLoop() != null) ss.setBgMusicLoop(request.getBgMusicLoop());
        if (request.getEffect() != null) ss.setEffect(request.getEffect());
        if (request.getEffectIntensity() != null) ss.setEffectIntensity(request.getEffectIntensity());
        if (request.getTitlePageText() != null) ss.setTitlePageText(request.getTitlePageText());
        if (request.getTextAnimation() != null) ss.setTextAnimation(request.getTextAnimation());
        if (request.getTextStyle() != null) ss.setTextStyle(request.getTextStyle());
        if (request.getTextPosition() != null) ss.setTextPosition(request.getTextPosition());
        if (request.getIsPublic() != null) ss.setIsPublic(request.getIsPublic());
        if (request.getAllowDownload() != null) ss.setAllowDownload(request.getAllowDownload());

        Slideshow saved = slideshowRepository.save(ss);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public void deleteSlideshow(Long id, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限删除"));
        slideshowItemRepository.deleteBySlideshowId(id);
        slideshowRepository.delete(ss);
    }

    public SlideshowDTO getSlideshow(Long id, Long userId) {
        Slideshow ss = slideshowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在"));

        boolean isOwner = ss.getUserId().equals(userId);
        if (!isOwner && !Boolean.TRUE.equals(ss.getIsPublic())) {
            throw new RuntimeException("无权查看私密幻灯片");
        }

        if (Boolean.TRUE.equals(ss.getIsPublic()) && !isOwner) {
            ss.setViewCount(ss.getViewCount() + 1);
            slideshowRepository.save(ss);
        }

        return toDTO(ss, userId, true);
    }

    public SlideshowDTO getSlideshowByShareToken(String token) {
        Slideshow ss = slideshowRepository.findByShareToken(token)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在"));

        ss.setViewCount(ss.getViewCount() + 1);
        slideshowRepository.save(ss);

        return toDTO(ss, null, true);
    }

    public SlideshowDTO getSlideshowForEdit(Long id, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));
        return toDTO(ss, userId, true);
    }

    public List<SlideshowDTO> listMySlideshows(Long userId) {
        List<Slideshow> slideshows = slideshowRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return slideshows.stream().map(s -> toDTO(s, userId, false)).collect(Collectors.toList());
    }

    @Transactional
    public SlideshowDTO publishSlideshow(Long id, boolean isPublic, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));

        long itemCount = slideshowItemRepository.countBySlideshowId(id);
        if (itemCount == 0) {
            throw new RuntimeException("请至少添加一张图片后再发布");
        }

        ss.setStatus(1);
        ss.setIsPublic(isPublic);
        ss.setPublishTime(new Date());

        if (ss.getCoverUrl() == null) {
            List<SlideshowItem> items = slideshowItemRepository.findBySlideshowIdOrderBySortOrderAscCreateTimeAsc(id);
            if (!items.isEmpty()) {
                Picture pic = pictureRepository.findById(items.get(0).getPictureId()).orElse(null);
                if (pic != null) {
                    ss.setCoverUrl(pic.getUrl());
                }
            }
        }

        Slideshow saved = slideshowRepository.save(ss);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public SlideshowDTO unpublishSlideshow(Long id, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));

        ss.setStatus(0);
        ss.setIsPublic(false);
        ss.setPublishTime(null);

        Slideshow saved = slideshowRepository.save(ss);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public SlideshowItemDTO addSlideshowItem(Long slideshowId, SlideshowItemAddRequest request, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(slideshowId, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));

        Picture picture = pictureRepository.findById(request.getPictureId()).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在或无权限");
        }

        SlideshowItem item = new SlideshowItem();
        item.setSlideshowId(slideshowId);
        item.setPictureId(request.getPictureId());
        item.setCaption(request.getCaption());
        item.setEffect(request.getEffect() != null ? request.getEffect() : "none");
        item.setEffectIntensity(request.getEffectIntensity() != null ? request.getEffectIntensity() : 0.5);
        item.setTextAnimation(request.getTextAnimation() != null ? request.getTextAnimation() : "fade");
        item.setTextStyle(request.getTextStyle() != null ? request.getTextStyle() : "{}");
        item.setTextPosition(request.getTextPosition() != null ? request.getTextPosition() : "bottom");
        item.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        item.setCustomInterval(request.getCustomInterval());

        SlideshowItem saved = slideshowItemRepository.save(item);

        if (ss.getCoverUrl() == null) {
            ss.setCoverUrl(picture.getUrl());
            slideshowRepository.save(ss);
        }

        return toItemDTO(saved);
    }

    @Transactional
    public SlideshowItemDTO updateSlideshowItem(Long itemId, SlideshowItemUpdateRequest request, Long userId) {
        SlideshowItem item = slideshowItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("幻灯片条目不存在"));

        Slideshow ss = slideshowRepository.findByIdAndUserId(item.getSlideshowId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此幻灯片"));

        if (request.getCaption() != null) item.setCaption(request.getCaption());
        if (request.getEffect() != null) item.setEffect(request.getEffect());
        if (request.getEffectIntensity() != null) item.setEffectIntensity(request.getEffectIntensity());
        if (request.getTextAnimation() != null) item.setTextAnimation(request.getTextAnimation());
        if (request.getTextStyle() != null) item.setTextStyle(request.getTextStyle());
        if (request.getTextPosition() != null) item.setTextPosition(request.getTextPosition());
        if (request.getSortOrder() != null) item.setSortOrder(request.getSortOrder());
        if (request.getCustomInterval() != null) item.setCustomInterval(request.getCustomInterval());

        SlideshowItem saved = slideshowItemRepository.save(item);
        return toItemDTO(saved);
    }

    @Transactional
    public void removeSlideshowItem(Long itemId, Long userId) {
        SlideshowItem item = slideshowItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("幻灯片条目不存在"));

        Slideshow ss = slideshowRepository.findByIdAndUserId(item.getSlideshowId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此幻灯片"));

        slideshowItemRepository.delete(item);

        long remaining = slideshowItemRepository.countBySlideshowId(item.getSlideshowId());
        if (remaining == 0) {
            ss.setCoverUrl(null);
            slideshowRepository.save(ss);
        }
    }

    public List<SlideshowItemDTO> listSlideshowItems(Long slideshowId, Long userId) {
        Slideshow ss = slideshowRepository.findById(slideshowId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在"));

        boolean isOwner = ss.getUserId().equals(userId);
        if (!isOwner && !Boolean.TRUE.equals(ss.getIsPublic())) {
            throw new RuntimeException("无权查看私密幻灯片");
        }

        List<SlideshowItem> items = slideshowItemRepository.findBySlideshowIdOrderBySortOrderAscCreateTimeAsc(slideshowId);
        return items.stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    @Transactional
    public void reorderItems(Long slideshowId, List<Long> itemIds, Long userId) {
        Slideshow ss = slideshowRepository.findByIdAndUserId(slideshowId, userId)
                .orElseThrow(() -> new RuntimeException("幻灯片不存在或无权限"));

        for (int i = 0; i < itemIds.size(); i++) {
            final int sortOrder = i;
            Long itemId = itemIds.get(i);
            slideshowItemRepository.findById(itemId).ifPresent(item -> {
                if (item.getSlideshowId().equals(slideshowId)) {
                    item.setSortOrder(sortOrder);
                    slideshowItemRepository.save(item);
                }
            });
        }
    }

    @Transactional
    private void addPicturesToSlideshow(Long slideshowId, List<Long> pictureIds, Long userId) {
        int order = 0;
        for (Long picId : pictureIds) {
            Picture picture = pictureRepository.findById(picId).orElse(null);
            if (picture != null && picture.getUserId().equals(userId)) {
                SlideshowItem item = new SlideshowItem();
                item.setSlideshowId(slideshowId);
                item.setPictureId(picId);
                item.setSortOrder(order++);
                slideshowItemRepository.save(item);
            }
        }

        Slideshow ss = slideshowRepository.findById(slideshowId).orElse(null);
        if (ss != null && ss.getCoverUrl() == null && !pictureIds.isEmpty()) {
            Picture firstPic = pictureRepository.findById(pictureIds.get(0)).orElse(null);
            if (firstPic != null) {
                ss.setCoverUrl(firstPic.getUrl());
                slideshowRepository.save(ss);
            }
        }
    }

    @Transactional
    private void addAlbumToSlideshow(Long slideshowId, Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null || !album.getUserId().equals(userId)) {
            throw new RuntimeException("专辑不存在或无权限");
        }

        List<Picture> pictures = pictureRepository.findByAlbumId(albumId);
        int order = 0;
        for (Picture pic : pictures) {
            SlideshowItem item = new SlideshowItem();
            item.setSlideshowId(slideshowId);
            item.setPictureId(pic.getId());
            item.setSortOrder(order++);
            slideshowItemRepository.save(item);
        }

        Slideshow ss = slideshowRepository.findById(slideshowId).orElse(null);
        if (ss != null && ss.getCoverUrl() == null && !pictures.isEmpty()) {
            ss.setCoverUrl(pictures.get(0).getUrl());
            slideshowRepository.save(ss);
        }
    }

    private SlideshowDTO toDTO(Slideshow ss, Long currentUserId, boolean includeItems) {
        SlideshowDTO dto = new SlideshowDTO();
        dto.setId(ss.getId());
        dto.setUserId(ss.getUserId());
        dto.setTitle(ss.getTitle());
        dto.setDescription(ss.getDescription());
        dto.setCoverUrl(ss.getCoverUrl());
        dto.setTransition(ss.getTransition());
        dto.setInterval(ss.getInterval());
        dto.setLoop(ss.getLoop());
        dto.setAutoStart(ss.getAutoStart());
        dto.setBgMusicUrl(ss.getBgMusicUrl());
        dto.setBgMusicType(ss.getBgMusicType());
        dto.setBgMusicVolume(ss.getBgMusicVolume());
        dto.setBgMusicLoop(ss.getBgMusicLoop());
        dto.setEffect(ss.getEffect());
        dto.setEffectIntensity(ss.getEffectIntensity());
        dto.setTitlePageText(ss.getTitlePageText());
        dto.setTextAnimation(ss.getTextAnimation());
        dto.setTextStyle(ss.getTextStyle());
        dto.setTextPosition(ss.getTextPosition());
        dto.setStatus(ss.getStatus());
        dto.setIsPublic(ss.getIsPublic());
        dto.setShareToken(ss.getShareToken());
        dto.setAllowDownload(ss.getAllowDownload());
        dto.setViewCount(ss.getViewCount());
        dto.setCreateTime(ss.getCreateTime());
        dto.setUpdateTime(ss.getUpdateTime());
        dto.setPublishTime(ss.getPublishTime());
        dto.setShareUrl("#/slideshow/" + ss.getShareToken());

        long itemCount = slideshowItemRepository.countBySlideshowId(ss.getId());
        dto.setItemCount((int) itemCount);

        User author = userRepository.findById(ss.getUserId()).orElse(null);
        if (author != null) {
            dto.setAuthorNickname(author.getNickname() != null ? author.getNickname() : author.getUsername());
        }

        if (currentUserId != null) {
            dto.setIsOwner(ss.getUserId().equals(currentUserId));
        } else {
            dto.setIsOwner(false);
        }

        if (includeItems) {
            List<SlideshowItem> items = slideshowItemRepository.findBySlideshowIdOrderBySortOrderAscCreateTimeAsc(ss.getId());
            dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private SlideshowItemDTO toItemDTO(SlideshowItem item) {
        SlideshowItemDTO dto = new SlideshowItemDTO();
        dto.setId(item.getId());
        dto.setSlideshowId(item.getSlideshowId());
        dto.setPictureId(item.getPictureId());
        dto.setCaption(item.getCaption());
        dto.setEffect(item.getEffect());
        dto.setEffectIntensity(item.getEffectIntensity());
        dto.setTextAnimation(item.getTextAnimation());
        dto.setTextStyle(item.getTextStyle());
        dto.setTextPosition(item.getTextPosition());
        dto.setSortOrder(item.getSortOrder());
        dto.setCustomInterval(item.getCustomInterval());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());

        Picture picture = pictureRepository.findById(item.getPictureId()).orElse(null);
        if (picture != null) {
            dto.setPictureUrl(picture.getUrl());
            dto.setPictureName(picture.getName());
        }

        return dto;
    }
}
