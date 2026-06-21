package com.example.picture.repository;

import com.example.picture.entity.StoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoryItemRepository extends JpaRepository<StoryItem, Long> {

    List<StoryItem> findByStoryIdOrderBySortOrderAscCreateTimeAsc(Long storyId);

    void deleteByStoryId(Long storyId);

    long countByStoryId(Long storyId);
}
