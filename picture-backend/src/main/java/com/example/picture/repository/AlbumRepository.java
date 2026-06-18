package com.example.picture.repository;

import com.example.picture.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByUserIdAndIsDefaultTrue(Long userId);

    List<Album> findByUserIdOrderByDisplayOrderAscCreateTimeAsc(Long userId);

    List<Album> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword);

    boolean existsByUserIdAndName(Long userId, String name);

    @Query("SELECT COUNT(a) FROM Album a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    Optional<Album> findByIdAndUserId(Long id, Long userId);
}
