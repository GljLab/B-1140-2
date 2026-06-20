package com.example.picture.service;

import com.example.picture.dto.CollaborationInviteDTO;
import com.example.picture.dto.CollaboratorDTO;
import com.example.picture.entity.Album;
import com.example.picture.entity.AlbumCollaborator;
import com.example.picture.entity.CollaborationInvite;
import com.example.picture.entity.Notification;
import com.example.picture.entity.User;
import com.example.picture.repository.AlbumCollaboratorRepository;
import com.example.picture.repository.AlbumRepository;
import com.example.picture.repository.CollaborationInviteRepository;
import com.example.picture.repository.NotificationRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollaborationService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumCollaboratorRepository collaboratorRepository;

    @Autowired
    private CollaborationInviteRepository inviteRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public CollaborationInviteDTO inviteCollaborator(Long albumId, String inviteeUsername, Long inviterId) {
        Album album = albumRepository.findByIdAndUserId(albumId, inviterId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或您无权限管理"));

        if (album.getIsDefault()) {
            throw new RuntimeException("默认专辑不能邀请协作者");
        }

        User invitee = userRepository.findByUsername(inviteeUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (invitee.getId().equals(inviterId)) {
            throw new RuntimeException("不能邀请自己");
        }

        if (collaboratorRepository.existsByAlbumIdAndUserId(albumId, invitee.getId())) {
            throw new RuntimeException("该用户已是协作者");
        }

        if (inviteRepository.existsByAlbumIdAndInviteeIdAndStatus(albumId, invitee.getId(), "PENDING")) {
            throw new RuntimeException("已向该用户发送过邀请");
        }

        CollaborationInvite invite = new CollaborationInvite();
        invite.setAlbumId(albumId);
        invite.setInviterId(inviterId);
        invite.setInviteeId(invitee.getId());
        invite.setStatus("PENDING");
        invite = inviteRepository.save(invite);

        User inviter = userRepository.findById(inviterId).orElse(null);
        String inviterName = inviter != null ? (inviter.getNickname() != null ? inviter.getNickname() : inviter.getUsername()) : "用户";

        Notification notification = new Notification();
        notification.setUserId(invitee.getId());
        notification.setType("COLLABORATION_INVITE");
        notification.setContent(inviterName + "邀请你协作管理专辑\"" + album.getName() + "\"");
        notification.setRelatedId(invite.getId());
        notification.setRelatedType("COLLABORATION_INVITE");
        notification.setIsRead(false);
        notificationRepository.save(notification);

        return toInviteDTO(invite);
    }

    @Transactional
    public CollaborationInviteDTO acceptInvite(Long inviteId, Long userId) {
        CollaborationInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("邀请不存在"));

        if (!invite.getInviteeId().equals(userId)) {
            throw new RuntimeException("无权处理此邀请");
        }

        if (!"PENDING".equals(invite.getStatus())) {
            throw new RuntimeException("邀请已被处理");
        }

        Album album = albumRepository.findById(invite.getAlbumId())
                .orElseThrow(() -> new RuntimeException("专辑不存在"));

        invite.setStatus("ACCEPTED");
        invite = inviteRepository.save(invite);

        AlbumCollaborator collaborator = new AlbumCollaborator();
        collaborator.setAlbumId(album.getId());
        collaborator.setUserId(userId);
        collaborator.setRole("COLLABORATOR");
        collaboratorRepository.save(collaborator);

        return toInviteDTO(invite);
    }

    @Transactional
    public CollaborationInviteDTO rejectInvite(Long inviteId, Long userId) {
        CollaborationInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("邀请不存在"));

        if (!invite.getInviteeId().equals(userId)) {
            throw new RuntimeException("无权处理此邀请");
        }

        if (!"PENDING".equals(invite.getStatus())) {
            throw new RuntimeException("邀请已被处理");
        }

        invite.setStatus("REJECTED");
        return toInviteDTO(inviteRepository.save(invite));
    }

    public List<CollaboratorDTO> getCollaborators(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));

        boolean hasAccess = album.getUserId().equals(userId) ||
                collaboratorRepository.existsByAlbumIdAndUserId(albumId, userId);

        if (!hasAccess) {
            throw new RuntimeException("无权查看此专辑的协作者");
        }

        List<AlbumCollaborator> collaborators = collaboratorRepository.findByAlbumId(albumId);
        List<CollaboratorDTO> result = new ArrayList<>();

        User owner = userRepository.findById(album.getUserId()).orElse(null);
        if (owner != null) {
            CollaboratorDTO ownerDTO = new CollaboratorDTO();
            ownerDTO.setUserId(owner.getId());
            ownerDTO.setUsername(owner.getUsername());
            ownerDTO.setNickname(owner.getNickname());
            ownerDTO.setRole("OWNER");
            ownerDTO.setJoinedTime(album.getCreateTime());
            Long picCount = collaboratorRepository.countPicturesByAlbumIdAndUserId(albumId, owner.getId());
            ownerDTO.setContributedPictures(picCount != null ? picCount.intValue() : 0);
            result.add(ownerDTO);
        }

        for (AlbumCollaborator c : collaborators) {
            User user = userRepository.findById(c.getUserId()).orElse(null);
            if (user != null) {
                CollaboratorDTO dto = new CollaboratorDTO();
                dto.setId(c.getId());
                dto.setUserId(c.getUserId());
                dto.setUsername(user.getUsername());
                dto.setNickname(user.getNickname());
                dto.setRole(c.getRole());
                dto.setJoinedTime(c.getJoinedTime());
                Long picCount = collaboratorRepository.countPicturesByAlbumIdAndUserId(albumId, c.getUserId());
                dto.setContributedPictures(picCount != null ? picCount.intValue() : 0);
                result.add(dto);
            }
        }

        return result;
    }

    @Transactional
    public void removeCollaborator(Long albumId, Long collaboratorUserId, Long operatorId) {
        Album album = albumRepository.findByIdAndUserId(albumId, operatorId)
                .orElseThrow(() -> new RuntimeException("专辑不存在或您无权限管理"));

        if (album.getUserId().equals(collaboratorUserId)) {
            throw new RuntimeException("不能移除专辑创建者");
        }

        AlbumCollaborator collaborator = collaboratorRepository.findByAlbumIdAndUserId(albumId, collaboratorUserId)
                .orElseThrow(() -> new RuntimeException("该用户不是协作者"));

        collaboratorRepository.delete(collaborator);
    }

    @Transactional
    public void leaveCollaboration(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("专辑不存在"));

        if (album.getUserId().equals(userId)) {
            throw new RuntimeException("专辑创建者不能退出协作，请删除专辑");
        }

        AlbumCollaborator collaborator = collaboratorRepository.findByAlbumIdAndUserId(albumId, userId)
                .orElseThrow(() -> new RuntimeException("您不是该专辑的协作者"));

        collaboratorRepository.delete(collaborator);
    }

    public List<CollaborationInviteDTO> getPendingInvites(Long userId) {
        List<CollaborationInvite> invites = inviteRepository.findByInviteeIdAndStatus(userId, "PENDING");
        return invites.stream().map(this::toInviteDTO).collect(Collectors.toList());
    }

    public boolean canEditAlbum(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) return false;
        if (album.getUserId().equals(userId)) return true;
        return collaboratorRepository.existsByAlbumIdAndUserId(albumId, userId);
    }

    public boolean canDeleteAlbum(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) return false;
        return album.getUserId().equals(userId);
    }

    public boolean canManageCollaborators(Long albumId, Long userId) {
        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) return false;
        return album.getUserId().equals(userId);
    }

    public boolean canDeletePicture(Long albumId, Long pictureId, Long userId) {
        return true;
    }

    private CollaborationInviteDTO toInviteDTO(CollaborationInvite invite) {
        CollaborationInviteDTO dto = new CollaborationInviteDTO();
        dto.setId(invite.getId());
        dto.setAlbumId(invite.getAlbumId());
        dto.setInviterId(invite.getInviterId());
        dto.setInviteeId(invite.getInviteeId());
        dto.setStatus(invite.getStatus());
        dto.setCreateTime(invite.getCreateTime());
        dto.setUpdateTime(invite.getUpdateTime());

        Album album = albumRepository.findById(invite.getAlbumId()).orElse(null);
        if (album != null) {
            dto.setAlbumName(album.getName());
        }

        User inviter = userRepository.findById(invite.getInviterId()).orElse(null);
        if (inviter != null) {
            dto.setInviterUsername(inviter.getUsername());
            dto.setInviterNickname(inviter.getNickname());
        }

        User invitee = userRepository.findById(invite.getInviteeId()).orElse(null);
        if (invitee != null) {
            dto.setInviteeUsername(invitee.getUsername());
            dto.setInviteeNickname(invitee.getNickname());
        }

        return dto;
    }
}
