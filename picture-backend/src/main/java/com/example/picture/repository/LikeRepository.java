package com.example.picture.repository;

import com.example.picture.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPictureId(Long userId, Long pictureId);
    long countByPictureId(Long pictureId);
    void deleteByUserIdAndPictureId(Long userId, Long pictureId);
    boolean existsByUserIdAndPictureId(Long userId, Long pictureId);
    long countByUserId(Long userId);
}
