package com.example.picture.repository;

import com.example.picture.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

    @Query("SELECT p FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p JOIN p.tags t WHERE t.id = :tagId AND p.userId = :userId AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByTagIdAndUserId(@Param("tagId") Long tagId, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p WHERE p.userId = :userId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByNameContainingKeywordAndUserId(@Param("keyword") String keyword, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p WHERE p.userId = :userId AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByUserIdOrderByCreateTimeDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.size), 0) FROM Picture p WHERE p.userId = :userId AND p.deleted = false")
    Long sumAllSizesByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.size), 0) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId AND p.deleted = false")
    Long sumSizesByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId AND p.deleted = false")
    Long countByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT MAX(p.createTime) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId AND p.deleted = false")
    java.util.Date findLastUploadTimeByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.size), 0) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.deleted = false")
    Long sumSizesByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT MAX(p.createTime) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.deleted = false")
    java.util.Date findLastUploadTimeByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT COUNT(p) FROM Picture p WHERE p.userId = :userId AND p.deleted = false")
    long countByUserId(Long userId);

    @Query("SELECT p FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT COUNT(p) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.deleted = false")
    Long countByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT p FROM Picture p WHERE p.userId = :userId AND p.deleted = true ORDER BY p.deleteTime DESC")
    List<Picture> findDeletedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Picture p WHERE p.userId = :userId AND p.deleted = true")
    long countDeletedByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Picture p WHERE p.deleted = true AND p.deleteTime < :expireTime")
    List<Picture> findExpiredDeletedPictures(@Param("expireTime") java.util.Date expireTime);

    @Query("SELECT p FROM Picture p WHERE p.id = :id AND p.userId = :userId AND p.deleted = true")
    Optional<Picture> findDeletedByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT p FROM Picture p WHERE p.isPublic = true AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findPublicPictures();

    @Query("SELECT p FROM Picture p WHERE p.isPublic = true AND p.deleted = false AND p.userId = :userId ORDER BY p.createTime DESC")
    List<Picture> findPublicPicturesByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Picture p LEFT JOIN FETCH p.albums LEFT JOIN FETCH p.tags WHERE p.userId = :userId AND p.deleted = false ORDER BY p.createTime DESC")
    List<Picture> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Picture p LEFT JOIN FETCH p.tags WHERE p.userId = :userId AND p.deleted = false")
    List<Picture> findByUserIdWithTags(@Param("userId") Long userId);
}
