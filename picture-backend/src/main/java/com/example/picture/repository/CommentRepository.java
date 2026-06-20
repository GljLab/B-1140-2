package com.example.picture.repository;

import com.example.picture.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPictureIdOrderByCreateTimeDesc(Long pictureId);
    long countByPictureId(Long pictureId);
    long countByUserId(Long userId);
    List<Comment> findByUserIdOrderByCreateTimeDesc(Long userId);
}
