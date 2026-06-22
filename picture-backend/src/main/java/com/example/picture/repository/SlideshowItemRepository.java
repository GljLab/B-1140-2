package com.example.picture.repository;

import com.example.picture.entity.SlideshowItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideshowItemRepository extends JpaRepository<SlideshowItem, Long> {

    List<SlideshowItem> findBySlideshowIdOrderBySortOrderAscCreateTimeAsc(Long slideshowId);

    long countBySlideshowId(Long slideshowId);

    void deleteBySlideshowId(Long slideshowId);
}
