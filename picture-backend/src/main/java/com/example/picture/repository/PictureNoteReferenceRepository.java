package com.example.picture.repository;

import com.example.picture.entity.PictureNoteReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PictureNoteReferenceRepository extends JpaRepository<PictureNoteReference, Long> {

    List<PictureNoteReference> findByNoteIdOrderBySortOrderAscCreateTimeAsc(Long noteId);

    List<PictureNoteReference> findByUserIdOrderByCreateTimeDesc(Long userId);

    long countByNoteId(Long noteId);

    void deleteByNoteId(Long noteId);

    Optional<PictureNoteReference> findByNoteIdAndPictureId(Long noteId, Long pictureId);

    boolean existsByNoteIdAndPictureId(Long noteId, Long pictureId);
}
