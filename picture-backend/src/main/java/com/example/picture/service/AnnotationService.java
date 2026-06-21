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
public class AnnotationService {

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private AnnotationCommentRepository annotationCommentRepository;

    @Autowired
    private AnnotationVersionRepository annotationVersionRepository;

    @Autowired
    private AnnotationReviewRepository annotationReviewRepository;

    @Autowired
    private AnnotationPermissionRepository annotationPermissionRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private boolean canAnnotate(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (picture.getUserId().equals(userId)) {
            return true;
        }
        Optional<AnnotationPermission> permissionOpt = annotationPermissionRepository.findByPictureIdAndUserId(pictureId, userId);
        if (permissionOpt.isPresent()) {
            return Boolean.TRUE.equals(permissionOpt.get().getCanAnnotate());
        }
        Optional<AnnotationPermission> publicPermission = annotationPermissionRepository.findByPictureIdAndUserIdIsNull(pictureId);
        if (publicPermission.isPresent()) {
            return Boolean.TRUE.equals(publicPermission.get().getCanAnnotate());
        }
        return Boolean.TRUE.equals(picture.getIsPublic());
    }

    private boolean canViewAnnotations(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (picture.getUserId().equals(userId)) {
            return true;
        }
        Optional<AnnotationPermission> permissionOpt = annotationPermissionRepository.findByPictureIdAndUserId(pictureId, userId);
        if (permissionOpt.isPresent()) {
            return Boolean.TRUE.equals(permissionOpt.get().getCanView());
        }
        Optional<AnnotationPermission> publicPermission = annotationPermissionRepository.findByPictureIdAndUserIdIsNull(pictureId);
        if (publicPermission.isPresent()) {
            return Boolean.TRUE.equals(publicPermission.get().getCanView()) && Boolean.TRUE.equals(publicPermission.get().getIsPublicAnnotations());
        }
        return Boolean.TRUE.equals(picture.getIsPublic());
    }

    @Transactional
    public AnnotationDTO createAnnotation(Long pictureId, Long userId, AnnotationCreateRequest request) {
        if (!canAnnotate(pictureId, userId)) {
            throw new RuntimeException("无权批注此图片");
        }
        if (request.getToolType() == null || request.getToolType().trim().isEmpty()) {
            throw new RuntimeException("批注工具类型不能为空");
        }
        Annotation annotation = new Annotation();
        annotation.setPictureId(pictureId);
        annotation.setUserId(userId);
        annotation.setVersionId(request.getVersionId());
        annotation.setToolType(request.getToolType());
        annotation.setStartX(request.getStartX());
        annotation.setStartY(request.getStartY());
        annotation.setEndX(request.getEndX());
        annotation.setEndY(request.getEndY());
        annotation.setPointsData(request.getPointsData());
        annotation.setColor(request.getColor());
        annotation.setStrokeWidth(request.getStrokeWidth());
        annotation.setTextContent(request.getTextContent());
        annotation.setIsResolved(false);
        annotation.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        Annotation saved = annotationRepository.save(annotation);
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture != null && !picture.getUserId().equals(userId)) {
            sendNotification(picture.getUserId(), "ANNOTATION", "您的图片收到新的批注", saved.getId(), "ANNOTATION");
        }
        return toAnnotationDTO(saved);
    }

    @Transactional
    public void deleteAnnotation(Long annotationId, Long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).orElse(null);
        if (annotation == null) {
            throw new RuntimeException("批注不存在");
        }
        Picture picture = pictureRepository.findById(annotation.getPictureId()).orElse(null);
        boolean isPictureOwner = picture != null && picture.getUserId().equals(userId);
        boolean isAnnotationOwner = annotation.getUserId().equals(userId);
        if (!isPictureOwner && !isAnnotationOwner) {
            throw new RuntimeException("无权删除此批注");
        }
        annotationCommentRepository.deleteByAnnotationId(annotationId);
        annotationRepository.delete(annotation);
    }

    @Transactional
    public AnnotationDTO resolveAnnotation(Long annotationId, Long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).orElse(null);
        if (annotation == null) {
            throw new RuntimeException("批注不存在");
        }
        Picture picture = pictureRepository.findById(annotation.getPictureId()).orElse(null);
        boolean isPictureOwner = picture != null && picture.getUserId().equals(userId);
        boolean isAnnotationOwner = annotation.getUserId().equals(userId);
        if (!isPictureOwner && !isAnnotationOwner) {
            throw new RuntimeException("无权标记此批注");
        }
        annotation.setIsResolved(true);
        annotation.setResolvedBy(userId);
        annotation.setResolvedTime(new Date());
        Annotation saved = annotationRepository.save(annotation);
        if (!annotation.getUserId().equals(userId)) {
            sendNotification(annotation.getUserId(), "ANNOTATION_RESOLVED", "您的批注已被标记为已解决", annotationId, "ANNOTATION");
        }
        return toAnnotationDTO(saved);
    }

    public List<AnnotationDTO> getAnnotations(Long pictureId, Long userId, Boolean resolved, Long versionId) {
        if (!canViewAnnotations(pictureId, userId)) {
            throw new RuntimeException("无权查看此图片的批注");
        }
        List<Annotation> annotations;
        if (versionId != null) {
            annotations = annotationRepository.findByPictureIdAndVersionIdOrderByCreateTimeDesc(pictureId, versionId);
        } else if (resolved != null) {
            annotations = annotationRepository.findByPictureIdAndIsResolvedOrderByCreateTimeDesc(pictureId, resolved);
        } else {
            annotations = annotationRepository.findByPictureIdOrderByCreateTimeDesc(pictureId);
        }
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        boolean isOwner = picture != null && picture.getUserId().equals(userId);
        return annotations.stream()
                .filter(a -> isOwner || a.getUserId().equals(userId) || Boolean.TRUE.equals(a.getIsPublic()))
                .map(this::toAnnotationDTO)
                .collect(Collectors.toList());
    }

    public AnnotationDTO getAnnotationDetail(Long annotationId, Long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).orElse(null);
        if (annotation == null) {
            throw new RuntimeException("批注不存在");
        }
        if (!canViewAnnotations(annotation.getPictureId(), userId)) {
            throw new RuntimeException("无权查看此批注");
        }
        AnnotationDTO dto = toAnnotationDTO(annotation);
        List<AnnotationCommentDTO> comments = annotationCommentRepository.findByAnnotationIdOrderByCreateTimeAsc(annotationId)
                .stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());
        dto.setComments(buildCommentTree(comments));
        dto.setCommentCount(comments.size());
        return dto;
    }

    @Transactional
    public AnnotationCommentDTO addComment(Long annotationId, Long userId, AnnotationCommentCreateRequest request) {
        Annotation annotation = annotationRepository.findById(annotationId).orElse(null);
        if (annotation == null) {
            throw new RuntimeException("批注不存在");
        }
        if (!canViewAnnotations(annotation.getPictureId(), userId)) {
            throw new RuntimeException("无权评论此批注");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        AnnotationComment comment = new AnnotationComment();
        comment.setAnnotationId(annotationId);
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent().trim());
        AnnotationComment saved = annotationCommentRepository.save(comment);
        if (!annotation.getUserId().equals(userId)) {
            sendNotification(annotation.getUserId(), "ANNOTATION_COMMENT", "您的批注收到新的评论", annotationId, "ANNOTATION");
        }
        if (request.getParentId() != null) {
            AnnotationComment parentComment = annotationCommentRepository.findById(request.getParentId()).orElse(null);
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                sendNotification(parentComment.getUserId(), "ANNOTATION_COMMENT_REPLY", "您的评论收到新回复", annotationId, "ANNOTATION");
            }
        }
        return toCommentDTO(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        AnnotationComment comment = annotationCommentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        annotationCommentRepository.delete(comment);
    }

    @Transactional
    public AnnotationVersionDTO createVersion(Long pictureId, Long userId, AnnotationVersionCreateRequest request) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId)) {
            throw new RuntimeException("只有图片所有者可以创建版本");
        }
        AnnotationVersion lastVersion = annotationVersionRepository.findTopByPictureIdOrderByVersionNumberDesc(pictureId).orElse(null);
        int nextVersionNumber = lastVersion != null ? lastVersion.getVersionNumber() + 1 : 1;
        AnnotationVersion version = new AnnotationVersion();
        version.setPictureId(pictureId);
        version.setUserId(userId);
        version.setVersionName(request.getVersionName() != null ? request.getVersionName() : "版本 " + nextVersionNumber);
        version.setDescription(request.getDescription());
        version.setVersionNumber(nextVersionNumber);
        AnnotationVersion saved = annotationVersionRepository.save(version);
        return toVersionDTO(saved);
    }

    public List<AnnotationVersionDTO> getVersions(Long pictureId, Long userId) {
        if (!canViewAnnotations(pictureId, userId)) {
            throw new RuntimeException("无权查看此图片的批注版本");
        }
        return annotationVersionRepository.findByPictureIdOrderByVersionNumberDesc(pictureId).stream()
                .map(this::toVersionDTO)
                .collect(Collectors.toList());
    }

    public AnnotationVersionDTO getVersionDetail(Long versionId, Long userId) {
        AnnotationVersion version = annotationVersionRepository.findById(versionId).orElse(null);
        if (version == null) {
            throw new RuntimeException("版本不存在");
        }
        if (!canViewAnnotations(version.getPictureId(), userId)) {
            throw new RuntimeException("无权查看此版本");
        }
        AnnotationVersionDTO dto = toVersionDTO(version);
        long count = annotationRepository.countByPictureId(version.getPictureId());
        dto.setAnnotationCount((int) count);
        return dto;
    }

    @Transactional
    public AnnotationReviewDTO createReview(Long requesterId, AnnotationReviewCreateRequest request) {
        Picture picture = pictureRepository.findById(request.getPictureId()).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(requesterId)) {
            throw new RuntimeException("只有图片所有者可以发起审阅");
        }
        if (request.getReviewerId() == null) {
            throw new RuntimeException("请选择审阅人");
        }
        User reviewer = userRepository.findById(request.getReviewerId()).orElse(null);
        if (reviewer == null) {
            throw new RuntimeException("审阅人不存在");
        }
        AnnotationReview review = new AnnotationReview();
        review.setPictureId(request.getPictureId());
        review.setRequesterId(requesterId);
        review.setReviewerId(request.getReviewerId());
        review.setStatus("PENDING");
        review.setComment(request.getComment());
        AnnotationReview saved = annotationReviewRepository.save(review);
        sendNotification(request.getReviewerId(), "REVIEW_REQUEST", "您收到新的图片审阅请求", saved.getId(), "REVIEW");
        return toReviewDTO(saved);
    }

    @Transactional
    public AnnotationReviewDTO updateReviewStatus(Long reviewId, Long reviewerId, AnnotationReviewUpdateRequest request) {
        AnnotationReview review = annotationReviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new RuntimeException("审阅不存在");
        }
        if (!review.getReviewerId().equals(reviewerId)) {
            throw new RuntimeException("无权操作此审阅");
        }
        String status = request.getStatus();
        if (!Arrays.asList("PENDING", "ANNOTATED", "APPROVED", "NEEDS_REVISION").contains(status)) {
            throw new RuntimeException("无效的审阅状态");
        }
        review.setStatus(status);
        review.setComment(request.getComment());
        if (!"PENDING".equals(status)) {
            review.setReviewTime(new Date());
        }
        AnnotationReview saved = annotationReviewRepository.save(review);
        sendNotification(review.getRequesterId(), "REVIEW_COMPLETED", "您发起的审阅已处理完成: " + status, reviewId, "REVIEW");
        return toReviewDTO(saved);
    }

    public List<AnnotationReviewDTO> getReviewsByPicture(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此图片的审阅");
        }
        return annotationReviewRepository.findByPictureIdOrderByCreateTimeDesc(pictureId).stream()
                .map(this::toReviewDTO)
                .collect(Collectors.toList());
    }

    public List<AnnotationReviewDTO> getMyReviews(Long reviewerId, String status) {
        List<AnnotationReview> reviews;
        if (status != null && !status.isEmpty()) {
            reviews = annotationReviewRepository.findByReviewerIdAndStatusOrderByCreateTimeDesc(reviewerId, status);
        } else {
            reviews = annotationReviewRepository.findByReviewerIdOrderByCreateTimeDesc(reviewerId);
        }
        return reviews.stream()
                .map(this::toReviewDTO)
                .collect(Collectors.toList());
    }

    public List<AnnotationReviewDTO> getMyRequestedReviews(Long requesterId) {
        return annotationReviewRepository.findByRequesterIdOrderByCreateTimeDesc(requesterId).stream()
                .map(this::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnnotationPermissionDTO setPermission(Long pictureId, Long ownerId, AnnotationPermissionRequest request) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || Boolean.TRUE.equals(picture.getDeleted())) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(ownerId)) {
            throw new RuntimeException("只有图片所有者可以设置权限");
        }
        AnnotationPermission permission;
        Optional<AnnotationPermission> existing;
        if (request.getUserId() != null) {
            existing = annotationPermissionRepository.findByPictureIdAndUserId(pictureId, request.getUserId());
        } else {
            existing = annotationPermissionRepository.findByPictureIdAndUserIdIsNull(pictureId);
        }
        if (existing.isPresent()) {
            permission = existing.get();
        } else {
            permission = new AnnotationPermission();
            permission.setPictureId(pictureId);
            permission.setUserId(request.getUserId());
        }
        if (request.getCanAnnotate() != null) {
            permission.setCanAnnotate(request.getCanAnnotate());
        }
        if (request.getCanView() != null) {
            permission.setCanView(request.getCanView());
        }
        if (request.getIsPublicAnnotations() != null) {
            permission.setIsPublicAnnotations(request.getIsPublicAnnotations());
        }
        AnnotationPermission saved = annotationPermissionRepository.save(permission);
        return toPermissionDTO(saved);
    }

    public List<AnnotationPermissionDTO> getPermissions(Long pictureId, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此图片的权限设置");
        }
        return annotationPermissionRepository.findByPictureId(pictureId).stream()
                .map(this::toPermissionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removePermission(Long pictureId, Long ownerId, Long targetUserId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null) {
            throw new RuntimeException("图片不存在");
        }
        if (!picture.getUserId().equals(ownerId)) {
            throw new RuntimeException("只有图片所有者可以删除权限");
        }
        annotationPermissionRepository.deleteByPictureIdAndUserId(pictureId, targetUserId);
    }

    public Map<String, Object> getAnnotationStats(Long pictureId, Long userId) {
        if (!canViewAnnotations(pictureId, userId)) {
            throw new RuntimeException("无权查看此数据");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", annotationRepository.countByPictureId(pictureId));
        stats.put("resolvedCount", annotationRepository.countByPictureIdAndIsResolved(pictureId, true));
        stats.put("unresolvedCount", annotationRepository.countByPictureIdAndIsResolved(pictureId, false));
        stats.put("versionCount", annotationVersionRepository.countByPictureId(pictureId));
        return stats;
    }

    private void sendNotification(Long userId, String type, String content, Long relatedId, String relatedType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    private List<AnnotationCommentDTO> buildCommentTree(List<AnnotationCommentDTO> comments) {
        Map<Long, AnnotationCommentDTO> commentMap = new LinkedHashMap<>();
        for (AnnotationCommentDTO comment : comments) {
            commentMap.put(comment.getId(), comment);
        }
        List<AnnotationCommentDTO> roots = new ArrayList<>();
        for (AnnotationCommentDTO comment : comments) {
            if (comment.getParentId() == null) {
                roots.add(comment);
            } else {
                AnnotationCommentDTO parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    if (parent.getComments() == null) {
                        parent.setComments(new ArrayList<>());
                    }
                    parent.getComments().add(comment);
                }
            }
        }
        return roots;
    }

    private AnnotationDTO toAnnotationDTO(Annotation annotation) {
        AnnotationDTO dto = new AnnotationDTO();
        dto.setId(annotation.getId());
        dto.setPictureId(annotation.getPictureId());
        dto.setVersionId(annotation.getVersionId());
        dto.setUserId(annotation.getUserId());
        dto.setToolType(annotation.getToolType());
        dto.setStartX(annotation.getStartX());
        dto.setStartY(annotation.getStartY());
        dto.setEndX(annotation.getEndX());
        dto.setEndY(annotation.getEndY());
        dto.setPointsData(annotation.getPointsData());
        dto.setColor(annotation.getColor());
        dto.setStrokeWidth(annotation.getStrokeWidth());
        dto.setTextContent(annotation.getTextContent());
        dto.setIsResolved(annotation.getIsResolved());
        dto.setResolvedBy(annotation.getResolvedBy());
        dto.setResolvedTime(annotation.getResolvedTime());
        dto.setIsPublic(annotation.getIsPublic());
        dto.setCreateTime(annotation.getCreateTime());
        dto.setUpdateTime(annotation.getUpdateTime());
        dto.setCommentCount((int) annotationCommentRepository.countByAnnotationId(annotation.getId()));
        userRepository.findById(annotation.getUserId()).ifPresent(user -> {
            dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        if (annotation.getResolvedBy() != null) {
            userRepository.findById(annotation.getResolvedBy()).ifPresent(user -> {
                dto.setResolvedByNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            });
        }
        return dto;
    }

    private AnnotationCommentDTO toCommentDTO(AnnotationComment comment) {
        AnnotationCommentDTO dto = new AnnotationCommentDTO();
        dto.setId(comment.getId());
        dto.setAnnotationId(comment.getAnnotationId());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParentId());
        dto.setContent(comment.getContent());
        dto.setCreateTime(comment.getCreateTime());
        userRepository.findById(comment.getUserId()).ifPresent(user -> {
            dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        if (comment.getParentId() != null) {
            annotationCommentRepository.findById(comment.getParentId()).ifPresent(parent -> {
                userRepository.findById(parent.getUserId()).ifPresent(user -> {
                    dto.setParentUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
                });
            });
        }
        return dto;
    }

    private AnnotationVersionDTO toVersionDTO(AnnotationVersion version) {
        AnnotationVersionDTO dto = new AnnotationVersionDTO();
        dto.setId(version.getId());
        dto.setPictureId(version.getPictureId());
        dto.setUserId(version.getUserId());
        dto.setVersionName(version.getVersionName());
        dto.setDescription(version.getDescription());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setCreateTime(version.getCreateTime());
        userRepository.findById(version.getUserId()).ifPresent(user -> {
            dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        return dto;
    }

    private AnnotationReviewDTO toReviewDTO(AnnotationReview review) {
        AnnotationReviewDTO dto = new AnnotationReviewDTO();
        dto.setId(review.getId());
        dto.setPictureId(review.getPictureId());
        dto.setRequesterId(review.getRequesterId());
        dto.setReviewerId(review.getReviewerId());
        dto.setStatus(review.getStatus());
        dto.setComment(review.getComment());
        dto.setRequestTime(review.getRequestTime());
        dto.setReviewTime(review.getReviewTime());
        dto.setCreateTime(review.getCreateTime());
        dto.setUpdateTime(review.getUpdateTime());
        userRepository.findById(review.getRequesterId()).ifPresent(user -> {
            dto.setRequesterNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        userRepository.findById(review.getReviewerId()).ifPresent(user -> {
            dto.setReviewerNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        });
        pictureRepository.findById(review.getPictureId()).ifPresent(picture -> {
            dto.setPictureName(picture.getName());
            dto.setPictureUrl(picture.getUrl());
        });
        return dto;
    }

    private AnnotationPermissionDTO toPermissionDTO(AnnotationPermission permission) {
        AnnotationPermissionDTO dto = new AnnotationPermissionDTO();
        dto.setId(permission.getId());
        dto.setPictureId(permission.getPictureId());
        dto.setUserId(permission.getUserId());
        dto.setCanAnnotate(permission.getCanAnnotate());
        dto.setCanView(permission.getCanView());
        dto.setIsPublicAnnotations(permission.getIsPublicAnnotations());
        dto.setCreateTime(permission.getCreateTime());
        dto.setUpdateTime(permission.getUpdateTime());
        if (permission.getUserId() != null) {
            userRepository.findById(permission.getUserId()).ifPresent(user -> {
                dto.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            });
        }
        return dto;
    }
}
