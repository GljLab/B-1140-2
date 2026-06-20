package com.example.picture.repository;

import com.example.picture.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndPictureId(Long userId, Long pictureId);
    long countByPictureId(Long pictureId);
    void deleteByUserIdAndPictureId(Long userId, Long pictureId);
    boolean existsByUserIdAndPictureId(Long userId, Long pictureId);

    @Query("SELECT f FROM Favorite f WHERE f.userId = :userId ORDER BY f.createTime DESC")
    List<Favorite> findByUserIdOrderByCreateTimeDesc(@Param("userId") Long userId);
}
