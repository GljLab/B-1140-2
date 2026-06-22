package com.example.picture.repository;

import com.example.picture.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT DISTINCT sh.keyword FROM SearchHistory sh WHERE sh.userId = :userId ORDER BY sh.searchTime DESC")
    List<String> findDistinctKeywordsByUserId(@Param("userId") Long userId);

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId ORDER BY sh.searchTime DESC")
    List<SearchHistory> findByUserIdOrderBySearchTimeDesc(@Param("userId") Long userId);

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId ORDER BY sh.searchTime DESC")
    List<SearchHistory> findTop10ByUserIdOrderBySearchTimeDesc(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    @Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId AND LOWER(sh.keyword) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY sh.searchTime DESC")
    List<SearchHistory> findByUserIdAndKeywordContaining(@Param("userId") Long userId, @Param("keyword") String keyword);
}
