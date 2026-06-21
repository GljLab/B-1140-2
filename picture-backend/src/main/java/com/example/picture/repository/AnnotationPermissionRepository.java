package com.example.picture.repository;

import com.example.picture.entity.AnnotationPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnotationPermissionRepository extends JpaRepository<AnnotationPermission, Long> {
    Optional<AnnotationPermission> findByPictureIdAndUserId(Long pictureId, Long userId);

    List<AnnotationPermission> findByPictureId(Long pictureId);

    Optional<AnnotationPermission> findByPictureIdAndUserIdIsNull(Long pictureId);

    void deleteByPictureIdAndUserId(Long pictureId, Long userId);
}
