package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annotations")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @PostMapping("/pictures/{pictureId}")
    public ResponseEntity<ApiResponse<AnnotationDTO>> createAnnotation(
            @PathVariable Long pictureId,
            @RequestBody AnnotationCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationDTO annotation = annotationService.createAnnotation(pictureId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("批注创建成功", annotation));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}")
    public ResponseEntity<ApiResponse<List<AnnotationDTO>>> getAnnotations(
            @PathVariable Long pictureId,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(required = false) Long versionId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<AnnotationDTO> annotations = annotationService.getAnnotations(pictureId, userId, resolved, versionId);
            return ResponseEntity.ok(ApiResponse.success(annotations));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{annotationId}")
    public ResponseEntity<ApiResponse<AnnotationDTO>> getAnnotationDetail(@PathVariable Long annotationId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationDTO annotation = annotationService.getAnnotationDetail(annotationId, userId);
            return ResponseEntity.ok(ApiResponse.success(annotation));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{annotationId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnotation(@PathVariable Long annotationId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            annotationService.deleteAnnotation(annotationId, userId);
            return ResponseEntity.ok(ApiResponse.success("批注已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{annotationId}/resolve")
    public ResponseEntity<ApiResponse<AnnotationDTO>> resolveAnnotation(@PathVariable Long annotationId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationDTO annotation = annotationService.resolveAnnotation(annotationId, userId);
            return ResponseEntity.ok(ApiResponse.success("批注已标记为已解决", annotation));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{annotationId}/comments")
    public ResponseEntity<ApiResponse<AnnotationCommentDTO>> addComment(
            @PathVariable Long annotationId,
            @RequestBody AnnotationCommentCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationCommentDTO comment = annotationService.addComment(annotationId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("评论成功", comment));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            annotationService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.success("评论已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/{pictureId}/versions")
    public ResponseEntity<ApiResponse<AnnotationVersionDTO>> createVersion(
            @PathVariable Long pictureId,
            @RequestBody AnnotationVersionCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationVersionDTO version = annotationService.createVersion(pictureId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("版本创建成功", version));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/versions")
    public ResponseEntity<ApiResponse<List<AnnotationVersionDTO>>> getVersions(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<AnnotationVersionDTO> versions = annotationService.getVersions(pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success(versions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/versions/{versionId}")
    public ResponseEntity<ApiResponse<AnnotationVersionDTO>> getVersionDetail(@PathVariable Long versionId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationVersionDTO version = annotationService.getVersionDetail(versionId, userId);
            return ResponseEntity.ok(ApiResponse.success(version));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<AnnotationReviewDTO>> createReview(@RequestBody AnnotationReviewCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationReviewDTO review = annotationService.createReview(userId, request);
            return ResponseEntity.ok(ApiResponse.success("审阅请求已发送", review));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<AnnotationReviewDTO>> updateReviewStatus(
            @PathVariable Long reviewId,
            @RequestBody AnnotationReviewUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationReviewDTO review = annotationService.updateReviewStatus(reviewId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("审阅状态已更新", review));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/reviews")
    public ResponseEntity<ApiResponse<List<AnnotationReviewDTO>>> getReviewsByPicture(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<AnnotationReviewDTO> reviews = annotationService.getReviewsByPicture(pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success(reviews));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/reviews/mine")
    public ResponseEntity<ApiResponse<List<AnnotationReviewDTO>>> getMyReviews(@RequestParam(required = false) String status) {
        Long userId = UserContext.getCurrentUserId();
        List<AnnotationReviewDTO> reviews = annotationService.getMyReviews(userId, status);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/reviews/requested")
    public ResponseEntity<ApiResponse<List<AnnotationReviewDTO>>> getMyRequestedReviews() {
        Long userId = UserContext.getCurrentUserId();
        List<AnnotationReviewDTO> reviews = annotationService.getMyRequestedReviews(userId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping("/pictures/{pictureId}/permissions")
    public ResponseEntity<ApiResponse<AnnotationPermissionDTO>> setPermission(
            @PathVariable Long pictureId,
            @RequestBody AnnotationPermissionRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            AnnotationPermissionDTO permission = annotationService.setPermission(pictureId, userId, request);
            return ResponseEntity.ok(ApiResponse.success("权限设置成功", permission));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/permissions")
    public ResponseEntity<ApiResponse<List<AnnotationPermissionDTO>>> getPermissions(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<AnnotationPermissionDTO> permissions = annotationService.getPermissions(pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/pictures/{pictureId}/permissions/{userId}")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable Long pictureId,
            @PathVariable Long userId) {
        try {
            Long ownerId = UserContext.getCurrentUserId();
            annotationService.removePermission(pictureId, ownerId, userId);
            return ResponseEntity.ok(ApiResponse.success("权限已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{pictureId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnnotationStats(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            Map<String, Object> stats = annotationService.getAnnotationStats(pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
