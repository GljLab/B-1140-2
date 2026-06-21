package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryItemRepository storyItemRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoryLikeRepository storyLikeRepository;

    @Autowired
    private StoryCommentRepository storyCommentRepository;

    public StoryDTO createStory(StoryCreateRequest request, Long userId) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("故事标题不能为空");
        }
        if (request.getTitle().length() > 100) {
            throw new RuntimeException("故事标题不能超过100字符");
        }

        Story story = new Story();
        story.setUserId(userId);
        story.setTitle(request.getTitle().trim());
        story.setDescription(request.getDescription() != null && request.getDescription().length() <= 500 ?
                request.getDescription() : null);
        story.setLayout(request.getLayout() != null ? request.getLayout() : "classic");
        story.setStatus(0);
        story.setIsPublic(false);
        story.setViewCount(0L);
        story.setLikeCount(0L);
        story.setCommentCount(0L);

        Story saved = storyRepository.save(story);
        return toDTO(saved, userId, false);
    }

    public StoryDTO updateStory(Long id, StoryUpdateRequest request, Long userId) {
        Story story = storyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));

        if (request.getTitle() != null) {
            String title = request.getTitle().trim();
            if (title.isEmpty()) throw new RuntimeException("故事标题不能为空");
            if (title.length() > 100) throw new RuntimeException("故事标题不能超过100字符");
            story.setTitle(title);
        }
        if (request.getDescription() != null) {
            if (request.getDescription().length() > 500) throw new RuntimeException("描述不能超过500字符");
            story.setDescription(request.getDescription());
        }
        if (request.getLayout() != null) {
            story.setLayout(request.getLayout());
        }
        if (request.getCoverUrl() != null) {
            story.setCoverUrl(request.getCoverUrl());
        }
        if (request.getIsPublic() != null) {
            story.setIsPublic(request.getIsPublic());
        }

        Story saved = storyRepository.save(story);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public void deleteStory(Long id, Long userId) {
        Story story = storyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限删除"));
        storyItemRepository.deleteByStoryId(id);
        storyRepository.delete(story);
    }

    public StoryDTO getStory(Long id, Long userId) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("故事不存在"));

        boolean isOwner = story.getUserId().equals(userId);
        if (!isOwner && !Boolean.TRUE.equals(story.getIsPublic())) {
            throw new RuntimeException("无权查看私密故事");
        }
        if (story.getStatus() != 1 && !isOwner) {
            throw new RuntimeException("故事未发布");
        }

        if (Boolean.TRUE.equals(story.getIsPublic()) && !isOwner) {
            story.setViewCount(story.getViewCount() + 1);
            storyRepository.save(story);
        }

        return toDTO(story, userId, true);
    }

    public StoryDTO getStoryForEdit(Long id, Long userId) {
        Story story = storyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));
        return toDTO(story, userId, true);
    }

    public List<StoryDTO> listMyStories(Long userId) {
        List<Story> stories = storyRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return stories.stream().map(s -> toDTO(s, userId, false)).collect(Collectors.toList());
    }

    public List<StoryDTO> listMyStoriesByStatus(Long userId, Integer status) {
        List<Story> stories = storyRepository.findByUserIdAndStatusOrderByUpdateTimeDesc(userId, status);
        return stories.stream().map(s -> toDTO(s, userId, false)).collect(Collectors.toList());
    }

    public List<StoryDTO> listPublicStories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Story> stories = storyRepository.findPublicStories(pageable);
        return stories.stream().map(s -> toDTO(s, null, false)).collect(Collectors.toList());
    }

    public List<StoryDTO> searchPublicStories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Story> stories = storyRepository.searchPublicStories(keyword, pageable);
        return stories.stream().map(s -> toDTO(s, null, false)).collect(Collectors.toList());
    }

    @Transactional
    public StoryDTO publishStory(Long id, boolean isPublic, Long userId) {
        Story story = storyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));

        long itemCount = storyItemRepository.countByStoryId(id);
        if (itemCount == 0) {
            throw new RuntimeException("请至少添加一张图片后再发布");
        }

        story.setStatus(1);
        story.setIsPublic(isPublic);
        story.setPublishTime(new Date());

        if (story.getCoverUrl() == null) {
            List<StoryItem> items = storyItemRepository.findByStoryIdOrderBySortOrderAscCreateTimeAsc(id);
            if (!items.isEmpty()) {
                Picture pic = pictureRepository.findById(items.get(0).getPictureId()).orElse(null);
                if (pic != null) {
                    story.setCoverUrl(pic.getUrl());
                }
            }
        }

        Story saved = storyRepository.save(story);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public StoryDTO unpublishStory(Long id, Long userId) {
        Story story = storyRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));

        story.setStatus(0);
        story.setIsPublic(false);
        story.setPublishTime(null);

        Story saved = storyRepository.save(story);
        return toDTO(saved, userId, false);
    }

    @Transactional
    public StoryItemDTO addStoryItem(Long storyId, StoryItemAddRequest request, Long userId) {
        Story story = storyRepository.findByIdAndUserId(storyId, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));

        Picture picture = pictureRepository.findById(request.getPictureId()).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在或无权限");
        }

        StoryItem item = new StoryItem();
        item.setStoryId(storyId);
        item.setPictureId(request.getPictureId());
        item.setContent(request.getContent());
        item.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        StoryItem saved = storyItemRepository.save(item);

        if (story.getCoverUrl() == null) {
            story.setCoverUrl(picture.getUrl());
            storyRepository.save(story);
        }

        return toItemDTO(saved);
    }

    @Transactional
    public StoryItemDTO updateStoryItem(Long itemId, StoryItemUpdateRequest request, Long userId) {
        StoryItem item = storyItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("故事条目不存在"));

        Story story = storyRepository.findByIdAndUserId(item.getStoryId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此故事"));

        if (request.getContent() != null) {
            item.setContent(request.getContent());
        }
        if (request.getSortOrder() != null) {
            item.setSortOrder(request.getSortOrder());
        }

        StoryItem saved = storyItemRepository.save(item);
        return toItemDTO(saved);
    }

    @Transactional
    public void removeStoryItem(Long itemId, Long userId) {
        StoryItem item = storyItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("故事条目不存在"));

        Story story = storyRepository.findByIdAndUserId(item.getStoryId(), userId)
                .orElseThrow(() -> new RuntimeException("无权限操作此故事"));

        storyItemRepository.delete(item);

        long remainingCount = storyItemRepository.countByStoryId(item.getStoryId());
        if (remainingCount == 0) {
            story.setCoverUrl(null);
            storyRepository.save(story);
        }
    }

    public List<StoryItemDTO> listStoryItems(Long storyId, Long userId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("故事不存在"));

        boolean isOwner = story.getUserId().equals(userId);
        if (!isOwner && !Boolean.TRUE.equals(story.getIsPublic())) {
            throw new RuntimeException("无权查看私密故事");
        }

        List<StoryItem> items = storyItemRepository.findByStoryIdOrderBySortOrderAscCreateTimeAsc(storyId);
        return items.stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    @Transactional
    public void reorderItems(Long storyId, List<Long> itemIds, Long userId) {
        Story story = storyRepository.findByIdAndUserId(storyId, userId)
                .orElseThrow(() -> new RuntimeException("故事不存在或无权限"));

        for (int i = 0; i < itemIds.size(); i++) {
            final int sortOrder = i;
            Long itemId = itemIds.get(i);
            storyItemRepository.findById(itemId).ifPresent(item -> {
                if (item.getStoryId().equals(storyId)) {
                    item.setSortOrder(sortOrder);
                    storyItemRepository.save(item);
                }
            });
        }
    }

    private StoryDTO toDTO(Story story, Long currentUserId, boolean includeItems) {
        StoryDTO dto = new StoryDTO();
        dto.setId(story.getId());
        dto.setUserId(story.getUserId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setLayout(story.getLayout());
        dto.setStatus(story.getStatus());
        dto.setIsPublic(story.getIsPublic());
        dto.setViewCount(story.getViewCount());
        dto.setLikeCount(story.getLikeCount());
        dto.setCommentCount(story.getCommentCount());
        dto.setCreateTime(story.getCreateTime());
        dto.setUpdateTime(story.getUpdateTime());
        dto.setPublishTime(story.getPublishTime());

        long itemCount = storyItemRepository.countByStoryId(story.getId());
        dto.setItemCount((int) itemCount);

        User author = userRepository.findById(story.getUserId()).orElse(null);
        if (author != null) {
            dto.setAuthorNickname(author.getNickname() != null ? author.getNickname() : author.getUsername());
            dto.setAuthorUsername(author.getUsername());
        }

        if (currentUserId != null) {
            dto.setLiked(storyLikeRepository.existsByUserIdAndStoryId(currentUserId, story.getId()));
            dto.setIsOwner(story.getUserId().equals(currentUserId));
        } else {
            dto.setLiked(false);
            dto.setIsOwner(false);
        }

        if (includeItems) {
            List<StoryItem> items = storyItemRepository.findByStoryIdOrderBySortOrderAscCreateTimeAsc(story.getId());
            dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private StoryItemDTO toItemDTO(StoryItem item) {
        StoryItemDTO dto = new StoryItemDTO();
        dto.setId(item.getId());
        dto.setStoryId(item.getStoryId());
        dto.setPictureId(item.getPictureId());
        dto.setContent(item.getContent());
        dto.setSortOrder(item.getSortOrder());
        dto.setCreateTime(item.getCreateTime());
        dto.setUpdateTime(item.getUpdateTime());

        Picture picture = pictureRepository.findById(item.getPictureId()).orElse(null);
        if (picture != null) {
            dto.setPictureUrl(picture.getUrl());
            dto.setPictureName(picture.getName());
        }

        return dto;
    }

    public Map<String, Object> getMyStoryStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("draftCount", storyRepository.countByUserIdAndStatus(userId, 0));
        stats.put("publishedPublicCount", storyRepository.countPublishedPublicByUserId(userId));
        stats.put("publishedPrivateCount", storyRepository.countPublishedPrivateByUserId(userId));
        return stats;
    }
}
