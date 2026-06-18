package com.example.picture.repository;

import com.example.picture.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    Optional<Share> findByShareToken(String shareToken);

    List<Share> findByUserIdOrderByCreateTimeDesc(Long userId);

    Optional<Share> findByIdAndUserId(Long id, Long userId);
}
