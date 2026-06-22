package com.example.picture.repository;

import com.example.picture.entity.PictureNoteTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PictureNoteTemplateRepository extends JpaRepository<PictureNoteTemplate, Long> {

    List<PictureNoteTemplate> findByUserIdIsNullOrderBySortOrderAsc();

    List<PictureNoteTemplate> findByUserIdOrderBySortOrderAsc(Long userId);

    List<PictureNoteTemplate> findByUserIdOrUserIdIsNullOrderBySortOrderAsc(Long userId);

    List<PictureNoteTemplate> findByCategoryAndUserIdIsNullOrderBySortOrderAsc(String category);

    List<PictureNoteTemplate> findByCategoryAndUserIdOrderBySortOrderAsc(String category, Long userId);

    Optional<PictureNoteTemplate> findByIdAndUserId(Long id, Long userId);
}
