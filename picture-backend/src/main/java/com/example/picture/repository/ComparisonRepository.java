package com.example.picture.repository;

import com.example.picture.entity.Comparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComparisonRepository extends JpaRepository<Comparison, Long> {

    @Query("SELECT c FROM Comparison c WHERE c.userId = :userId ORDER BY c.createTime DESC")
    List<Comparison> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Comparison c WHERE c.id = :id AND c.userId = :userId")
    Optional<Comparison> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT c FROM Comparison c WHERE c.isPublic = true ORDER BY c.createTime DESC")
    List<Comparison> findPublicComparisons();

    @Query("SELECT c FROM Comparison c WHERE c.id = :id AND (c.userId = :userId OR c.isPublic = true)")
    Optional<Comparison> findAccessibleById(@Param("id") Long id, @Param("userId") Long userId);
}
