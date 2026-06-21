package com.example.picture.repository;

import com.example.picture.entity.AnnotationComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationCommentRepository extends JpaRepository<AnnotationComment, Long> {
    List<AnnotationComment> findByAnnotationIdOrderByCreateTimeAsc(Long annotationId);

    List<AnnotationComment> findByAnnotationIdAndParentIdOrderByCreateTimeAsc(Long annotationId, Long parentId);

    long countByAnnotationId(Long annotationId);

    void deleteByAnnotationId(Long annotationId);
}
