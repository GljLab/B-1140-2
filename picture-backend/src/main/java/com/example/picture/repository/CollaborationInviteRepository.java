package com.example.picture.repository;

import com.example.picture.entity.CollaborationInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollaborationInviteRepository extends JpaRepository<CollaborationInvite, Long> {

    List<CollaborationInvite> findByInviteeIdAndStatus(Long inviteeId, String status);

    List<CollaborationInvite> findByInviterId(Long inviterId);

    Optional<CollaborationInvite> findByAlbumIdAndInviteeIdAndStatus(Long albumId, Long inviteeId, String status);

    boolean existsByAlbumIdAndInviteeIdAndStatus(Long albumId, Long inviteeId, String status);
}
