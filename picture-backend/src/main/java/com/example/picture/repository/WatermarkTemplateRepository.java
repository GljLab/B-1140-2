package com.example.picture.repository;

import com.example.picture.entity.WatermarkTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatermarkTemplateRepository extends JpaRepository<WatermarkTemplate, Long> {

    List<WatermarkTemplate> findByIsSystemTrue();

    List<WatermarkTemplate> findByUserIdOrIsSystemTrue(Long userId);

    List<WatermarkTemplate> findByUserId(Long userId);
}
