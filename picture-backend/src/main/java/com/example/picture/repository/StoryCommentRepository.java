package com.example.picture.repository;

import com.example.picture.entity.StoryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoryCommentRepository extends JpaRepository<StoryComment, Long> {

    List<StoryComment> findByStoryIdOrderByCreateTimeDesc(Long storyId);

    long countByStoryId(Long storyId);

    long countByUserId(Long userId);
}
