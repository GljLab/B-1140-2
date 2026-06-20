package com.example.picture.repository;

import com.example.picture.entity.AlbumCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumCollaboratorRepository extends JpaRepository<AlbumCollaborator, Long> {

    List<AlbumCollaborator> findByAlbumId(Long albumId);

    List<AlbumCollaborator> findByUserId(Long userId);

    Optional<AlbumCollaborator> findByAlbumIdAndUserId(Long albumId, Long userId);

    void deleteByAlbumIdAndUserId(Long albumId, Long userId);

    boolean existsByAlbumIdAndUserId(Long albumId, Long userId);

    @Query("SELECT COUNT(DISTINCT p.id) FROM Picture p JOIN p.albums a WHERE a.id = :albumId AND p.userId = :userId AND p.deleted = false")
    Long countPicturesByAlbumIdAndUserId(@Param("albumId") Long albumId, @Param("userId") Long userId);
}
