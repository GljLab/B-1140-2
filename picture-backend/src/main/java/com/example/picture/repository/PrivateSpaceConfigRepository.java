package com.example.picture.repository;

import com.example.picture.entity.PrivateSpaceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateSpaceConfigRepository extends JpaRepository<PrivateSpaceConfig, Long> {

    Optional<PrivateSpaceConfig> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
