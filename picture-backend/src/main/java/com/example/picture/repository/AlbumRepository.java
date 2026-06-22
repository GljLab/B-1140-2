package com.example.picture.repository;

import com.example.picture.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByUserIdAndIsDefaultTrue(Long userId);

    List<Album> findByUserIdOrderByDisplayOrderAscCreateTimeAsc(Long userId);

    List<Album> findByUserIdAndNameContainingIgnoreCase(Long userId, String keyword);

    boolean existsByUserIdAndName(Long userId, String name);

    @Query("SELECT COUNT(a) FROM Album a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    Optional<Album> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT a FROM Album a JOIN AlbumCollaborator ac ON a.id = ac.albumId WHERE ac.userId = :userId ORDER BY a.createTime DESC")
    List<Album> findCollaborationAlbumsByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Album a WHERE a.id = :id AND (a.userId = :userId OR EXISTS (SELECT 1 FROM AlbumCollaborator ac WHERE ac.albumId = a.id AND ac.userId = :userId))")
    Optional<Album> findAccessibleAlbumById(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT DISTINCT a FROM Album a LEFT JOIN FETCH a.pictures WHERE a.userId = :userId ORDER BY a.displayOrder ASC, a.createTime ASC")
    List<Album> findByUserIdWithPictures(@Param("userId") Long userId);

    @Query("SELECT a FROM Album a WHERE a.userId = :userId AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Album> findByNameContainingAndUserId(@Param("name") String name, @Param("userId") Long userId);
}
