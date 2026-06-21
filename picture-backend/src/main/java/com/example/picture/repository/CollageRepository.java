package com.example.picture.repository;

import com.example.picture.entity.Collage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollageRepository extends JpaRepository<Collage, Long> {

    List<Collage> findByUserIdOrderByIdDesc(Long userId);
}
