package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/pictures/{pictureId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean liked = interactionService.toggleLike(pictureId, userId);
            long likeCount = interactionService.getLikeCount(pictureId);
            Map<String, Object> result = new HashMap<>();
            result.put("liked", liked);
            result.put("likeCount", likeCount);
            return ResponseEntity.ok(ApiResponse.success("操作成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/like-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikeStatus(@PathVariable Long pictureId) {
        Long userId = UserContext.getCurrentUserId();
        boolean liked = interactionService.isLiked(pictureId, userId);
        long likeCount = interactionService.getLikeCount(pictureId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/pictures/{pictureId}/comments")
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
            @PathVariable Long pictureId,
            @RequestBody CommentCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            CommentDTO comment = interactionService.addComment(pictureId, userId, request.getContent());
            return ResponseEntity.ok(ApiResponse.success("评论成功", comment));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            interactionService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.success("评论已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(@PathVariable Long pictureId) {
        List<CommentDTO> comments = interactionService.getComments(pictureId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping("/pictures/{pictureId}/favorite")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFavorite(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean favorited = interactionService.toggleFavorite(pictureId, userId);
            long favoriteCount = interactionService.getFavoriteCount(pictureId);
            Map<String, Object> result = new HashMap<>();
            result.put("favorited", favorited);
            result.put("favoriteCount", favoriteCount);
            return ResponseEntity.ok(ApiResponse.success("操作成功", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/favorite-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFavoriteStatus(@PathVariable Long pictureId) {
        Long userId = UserContext.getCurrentUserId();
        boolean favorited = interactionService.isFavorited(pictureId, userId);
        long favoriteCount = interactionService.getFavoriteCount(pictureId);
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        result.put("favoriteCount", favoriteCount);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/pictures/{pictureId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPictureStats(@PathVariable Long pictureId) {
        Long userId = UserContext.getCurrentUserId();
        long likeCount = interactionService.getLikeCount(pictureId);
        long commentCount = interactionService.getCommentCount(pictureId);
        long favoriteCount = interactionService.getFavoriteCount(pictureId);
        boolean liked = interactionService.isLiked(pictureId, userId);
        boolean favorited = interactionService.isFavorited(pictureId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("commentCount", commentCount);
        result.put("favoriteCount", favoriteCount);
        result.put("liked", liked);
        result.put("favorited", favorited);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
