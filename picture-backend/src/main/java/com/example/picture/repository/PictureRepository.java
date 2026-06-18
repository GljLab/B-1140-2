package com.example.picture.repository;

import com.example.picture.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {

    @Query("SELECT p FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId ORDER BY p.createTime DESC")
    List<Picture> findByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p JOIN p.tags t WHERE t.id = :tagId AND p.userId = :userId ORDER BY p.createTime DESC")
    List<Picture> findByTagIdAndUserId(@Param("tagId") Long tagId, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p WHERE p.userId = :userId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.createTime DESC")
    List<Picture> findByNameContainingKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") Long userId);

    List<Picture> findByUserIdOrderByCreateTimeDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.size), 0) FROM Picture p WHERE p.userId = :userId")
    Long sumAllSizesByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.size), 0) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId")
    Long sumSizesByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId")
    Long countByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT MAX(p.createTime) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId")
    java.util.Date findLastUploadTimeByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    long countByUserId(Long userId);
}
