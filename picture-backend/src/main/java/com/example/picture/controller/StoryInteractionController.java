package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.ApiResponse;
import com.example.picture.dto.StoryCommentDTO;
import com.example.picture.entity.Story;
import com.example.picture.entity.StoryComment;
import com.example.picture.entity.StoryLike;
import com.example.picture.entity.User;
import com.example.picture.repository.StoryCommentRepository;
import com.example.picture.repository.StoryLikeRepository;
import com.example.picture.repository.StoryRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/story-interactions")
public class StoryInteractionController {

    @Autowired
    private StoryLikeRepository storyLikeRepository;

    @Autowired
    private StoryCommentRepository storyCommentRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/stories/{storyId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable Long storyId) {
        try {
            Long userId = UserContext.getCurrentUserId();

            Story story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("故事不存在"));

            if (!story.getUserId().equals(userId) && !Boolean.TRUE.equals(story.getIsPublic())) {
                throw new RuntimeException("无法操作私密故事");
            }
            if (story.getStatus() != 1 && !story.getUserId().equals(userId)) {
                throw new RuntimeException("故事未发布");
            }

            boolean liked;
            if (storyLikeRepository.existsByUserIdAndStoryId(userId, storyId)) {
                storyLikeRepository.deleteByUserIdAndStoryId(userId, storyId);
                liked = false;
                story.setLikeCount(story.getLikeCount() - 1);
            } else {
                StoryLike like = new StoryLike();
                like.setUserId(userId);
                like.setStoryId(storyId);
                storyLikeRepository.save(like);
                liked = true;
                story.setLikeCount(story.getLikeCount() + 1);
            }
            storyRepository.save(story);

            Map<String, Object> result = new HashMap<>();
            result.put("liked", liked);
            result.put("likeCount", story.getLikeCount());
            return ResponseEntity.ok(ApiResponse.success("操作成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stories/{storyId}/like-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikeStatus(@PathVariable Long storyId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean liked = storyLikeRepository.existsByUserIdAndStoryId(userId, storyId);
            long likeCount = storyLikeRepository.countByStoryId(storyId);
            Map<String, Object> result = new HashMap<>();
            result.put("liked", liked);
            result.put("likeCount", likeCount);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/stories/{storyId}/comments")
    public ResponseEntity<ApiResponse<StoryCommentDTO>> addComment(
            @PathVariable Long storyId,
            @RequestBody Map<String, String> request) {
        try {
            Long userId = UserContext.getCurrentUserId();

            Story story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("故事不存在"));

            if (!story.getUserId().equals(userId) && !Boolean.TRUE.equals(story.getIsPublic())) {
                throw new RuntimeException("无法评论私密故事");
            }
            if (story.getStatus() != 1 && !story.getUserId().equals(userId)) {
                throw new RuntimeException("故事未发布");
            }

            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("评论内容不能为空");
            }
            if (content.length() > 500) {
                throw new RuntimeException("评论内容不能超过500字");
            }

            StoryComment comment = new StoryComment();
            comment.setUserId(userId);
            comment.setStoryId(storyId);
            comment.setContent(content.trim());
            StoryComment saved = storyCommentRepository.save(comment);

            story.setCommentCount(story.getCommentCount() + 1);
            storyRepository.save(story);

            return ResponseEntity.ok(ApiResponse.success("评论成功", toCommentDTO(saved)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        try {
            Long userId = UserContext.getCurrentUserId();

            StoryComment comment = storyCommentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("评论不存在"));

            Story story = storyRepository.findById(comment.getStoryId()).orElse(null);
            boolean isStoryAuthor = story != null && story.getUserId().equals(userId);
            boolean isCommentAuthor = comment.getUserId().equals(userId);

            if (!isStoryAuthor && !isCommentAuthor) {
                throw new RuntimeException("无权删除此评论");
            }

            storyCommentRepository.delete(comment);

            if (story != null) {
                story.setCommentCount(Math.max(0, story.getCommentCount() - 1));
                storyRepository.save(story);
            }

            return ResponseEntity.ok(ApiResponse.success("评论已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stories/{storyId}/comments")
    public ResponseEntity<ApiResponse<List<StoryCommentDTO>>> getComments(@PathVariable Long storyId) {
        try {
            Story story = storyRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("故事不存在"));

            Long userId = null;
            try {
                userId = UserContext.getCurrentUserId();
            } catch (Exception e) {
                // user not logged in
            }

            if (userId != null && !story.getUserId().equals(userId) && !Boolean.TRUE.equals(story.getIsPublic())) {
                throw new RuntimeException("无权查看评论");
            }
            if (userId == null && !Boolean.TRUE.equals(story.getIsPublic())) {
                throw new RuntimeException("无权查看评论");
            }

            List<StoryComment> comments = storyCommentRepository.findByStoryIdOrderByCreateTimeDesc(storyId);
            List<StoryCommentDTO> dtos = comments.stream()
                    .map(this::toCommentDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    private StoryCommentDTO toCommentDTO(StoryComment comment) {
        StoryCommentDTO dto = new StoryCommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setStoryId(comment.getStoryId());
        dto.setContent(comment.getContent());
        dto.setCreateTime(comment.getCreateTime());
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        return dto;
    }
}
