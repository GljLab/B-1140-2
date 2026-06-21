package com.example.picture.repository;

import com.example.picture.entity.StoryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoryLikeRepository extends JpaRepository<StoryLike, Long> {

    Optional<StoryLike> findByUserIdAndStoryId(Long userId, Long storyId);

    boolean existsByUserIdAndStoryId(Long userId, Long storyId);

    long countByStoryId(Long storyId);

    void deleteByUserIdAndStoryId(Long userId, Long storyId);
}
