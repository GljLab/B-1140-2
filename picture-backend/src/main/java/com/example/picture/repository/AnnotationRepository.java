package com.example.picture.repository;

import com.example.picture.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    List<Annotation> findByPictureIdOrderByCreateTimeDesc(Long pictureId);

    List<Annotation> findByPictureIdAndVersionIdOrderByCreateTimeDesc(Long pictureId, Long versionId);

    List<Annotation> findByPictureIdAndIsResolvedOrderByCreateTimeDesc(Long pictureId, Boolean isResolved);

    List<Annotation> findByPictureIdAndUserIdOrderByCreateTimeDesc(Long pictureId, Long userId);

    long countByPictureId(Long pictureId);

    long countByPictureIdAndIsResolved(Long pictureId, Boolean isResolved);

    @Modifying
    @Query("UPDATE Annotation a SET a.isResolved = true, a.resolvedBy = :resolvedBy, a.resolvedTime = CURRENT_TIMESTAMP WHERE a.id = :id")
    void markAsResolved(@Param("id") Long id, @Param("resolvedBy") Long resolvedBy);

    void deleteByPictureId(Long pictureId);
}
