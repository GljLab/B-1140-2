package com.example.picture.repository;

import com.example.picture.entity.AnnotationVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnotationVersionRepository extends JpaRepository<AnnotationVersion, Long> {
    List<AnnotationVersion> findByPictureIdOrderByVersionNumberDesc(Long pictureId);

    Optional<AnnotationVersion> findTopByPictureIdOrderByVersionNumberDesc(Long pictureId);

    long countByPictureId(Long pictureId);
}
