package com.example.picture.repository;

import com.example.picture.entity.Story;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Story> findByUserIdAndStatusOrderByUpdateTimeDesc(Long userId, Integer status);

    @Query("SELECT s FROM Story s WHERE s.isPublic = true AND s.status = 1 ORDER BY s.publishTime DESC")
    List<Story> findPublicStories(Pageable pageable);

    @Query("SELECT s FROM Story s WHERE s.isPublic = true AND s.status = 1 AND s.title LIKE %:keyword% ORDER BY s.publishTime DESC")
    List<Story> searchPublicStories(@Param("keyword") String keyword, Pageable pageable);

    Optional<Story> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, Integer status);

    @Query("SELECT COUNT(s) FROM Story s WHERE s.userId = :userId AND s.status = 1 AND s.isPublic = true")
    long countPublishedPublicByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Story s WHERE s.userId = :userId AND s.status = 1 AND s.isPublic = false")
    long countPublishedPrivateByUserId(@Param("userId") Long userId);
}
