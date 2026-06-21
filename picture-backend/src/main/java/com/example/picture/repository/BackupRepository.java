package com.example.picture.repository;

import com.example.picture.entity.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

    List<Backup> findByUserIdOrderByCreateTimeDesc(Long userId);

    Backup findByIdAndUserId(Long id, Long userId);
}
