package com.example.picture.repository;

import com.example.picture.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByUserIdAndName(Long userId, String name);

    List<Tag> findByUserIdAndNameContainingIgnoreCaseOrderByReferenceCountDesc(Long userId, String keyword);

    List<Tag> findByUserIdOrderByReferenceCountDescCreateTimeDesc(Long userId);

    @Query("SELECT t FROM Tag t WHERE t.userId = :userId AND t.referenceCount = 0")
    List<Tag> findUnusedTagsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Tag t WHERE t.userId = :userId ORDER BY t.referenceCount DESC, t.createTime DESC")
    List<Tag> findTopByUserIdOrderByReferenceCountDesc(@Param("userId") Long userId, Pageable pageable);

    Optional<Tag> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Query("SELECT t FROM Tag t WHERE t.userId = :userId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tag> findByNameContainingAndUserId(@Param("name") String name, @Param("userId") Long userId);
}
