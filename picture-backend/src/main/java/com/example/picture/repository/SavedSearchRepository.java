package com.example.picture.repository;

import com.example.picture.entity.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

    @Query("SELECT ss FROM SavedSearch ss WHERE ss.userId = :userId ORDER BY ss.createTime DESC")
    List<SavedSearch> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);

    @Query("SELECT ss FROM SavedSearch ss WHERE ss.id = :id AND ss.userId = :userId")
    Optional<SavedSearch> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
