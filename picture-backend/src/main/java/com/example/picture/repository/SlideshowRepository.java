package com.example.picture.repository;

import com.example.picture.entity.Slideshow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlideshowRepository extends JpaRepository<Slideshow, Long> {

    List<Slideshow> findByUserIdOrderByCreateTimeDesc(Long userId);

    Optional<Slideshow> findByIdAndUserId(Long id, Long userId);

    Optional<Slideshow> findByShareToken(String shareToken);

    long countByUserIdAndStatus(Long userId, Integer status);
}
