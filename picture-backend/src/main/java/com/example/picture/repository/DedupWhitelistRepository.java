package com.example.picture.repository;

import com.example.picture.entity.DedupWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DedupWhitelistRepository extends JpaRepository<DedupWhitelist, Long> {

    List<DedupWhitelist> findByUserId(Long userId);

    @Query("SELECT w FROM DedupWhitelist w WHERE w.userId = :userId " +
           "AND ((w.pictureId1 = :pid1 AND w.pictureId2 = :pid2) " +
           "OR (w.pictureId1 = :pid2 AND w.pictureId2 = :pid1)")
    DedupWhitelist findByUserAndPicturePair(@Param("userId") Long userId,
                                        @Param("pid1") Long pid1,
                                        @Param("pid2") Long pid2);

    @Query("SELECT w FROM DedupWhitelist w WHERE w.userId = :userId")
    List<DedupWhitelist> findAllByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndId(Long userId, Long id);
}
