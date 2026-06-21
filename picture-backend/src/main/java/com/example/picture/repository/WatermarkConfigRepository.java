package com.example.picture.repository;

import com.example.picture.entity.WatermarkConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatermarkConfigRepository extends JpaRepository<WatermarkConfig, Long> {

    Optional<WatermarkConfig> findByUserId(Long userId);
}
