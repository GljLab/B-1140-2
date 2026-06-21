package com.example.picture.repository;

import com.example.picture.entity.AnnotationReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationReviewRepository extends JpaRepository<AnnotationReview, Long> {
    List<AnnotationReview> findByPictureIdOrderByCreateTimeDesc(Long pictureId);

    List<AnnotationReview> findByReviewerIdOrderByCreateTimeDesc(Long reviewerId);

    List<AnnotationReview> findByRequesterIdOrderByCreateTimeDesc(Long requesterId);

    List<AnnotationReview> findByPictureIdAndStatusOrderByCreateTimeDesc(Long pictureId, String status);

    List<AnnotationReview> findByReviewerIdAndStatusOrderByCreateTimeDesc(Long reviewerId, String status);

    long countByReviewerIdAndStatus(Long reviewerId, String status);
}
