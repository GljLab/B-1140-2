package com.example.picture.repository;

import com.example.picture.entity.PictureNote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PictureNoteRepository extends JpaRepository<PictureNote, Long> {

    Optional<PictureNote> findByPictureIdAndUserId(Long pictureId, Long userId);

    List<PictureNote> findByUserIdOrderByUpdateTimeDesc(Long userId);

    List<PictureNote> findByUserIdAndIsDraftOrderByUpdateTimeDesc(Long userId, Boolean isDraft);

    @Query("SELECT n FROM PictureNote n WHERE n.userId = :userId AND n.content LIKE %:keyword% ORDER BY n.updateTime DESC")
    List<PictureNote> searchByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT n FROM PictureNote n WHERE n.userId = :userId AND n.title LIKE %:keyword% ORDER BY n.updateTime DESC")
    List<PictureNote> searchByUserIdAndTitleKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndIsDraft(Long userId, Boolean isDraft);

    @Query("SELECT COALESCE(SUM(n.wordCount), 0) FROM PictureNote n WHERE n.userId = :userId")
    Long sumWordCountByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM PictureNote n WHERE n.userId = :userId ORDER BY n.updateTime DESC")
    List<PictureNote> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM PictureNote n WHERE n.userId = :userId ORDER BY n.wordCount DESC")
    List<PictureNote> findTopByWordCount(@Param("userId") Long userId, Pageable pageable);

    boolean existsByPictureIdAndUserId(Long pictureId, Long userId);
}
