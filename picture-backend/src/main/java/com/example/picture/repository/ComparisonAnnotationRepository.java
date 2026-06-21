package com.example.picture.repository;

import com.example.picture.entity.ComparisonAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComparisonAnnotationRepository extends JpaRepository<ComparisonAnnotation, Long> {

    List<ComparisonAnnotation> findByComparisonIdOrderByCreateTimeAsc(Long comparisonId);

    List<ComparisonAnnotation> findByComparisonIdAndPictureIdOrderByCreateTimeAsc(Long comparisonId, Long pictureId);

    void deleteByComparisonId(Long comparisonId);
}
