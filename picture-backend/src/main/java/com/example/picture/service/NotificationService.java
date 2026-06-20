package com.example.picture.service;

import com.example.picture.dto.NotificationDTO;
import com.example.picture.entity.CollaborationInvite;
import com.example.picture.entity.Notification;
import com.example.picture.entity.User;
import com.example.picture.repository.CollaborationInviteRepository;
import com.example.picture.repository.NotificationRepository;
import com.example.picture.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CollaborationInviteRepository inviteRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long id, Long userId) {
        notificationRepository.markAsRead(id, userId);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setContent(notification.getContent());
        dto.setRelatedId(notification.getRelatedId());
        dto.setRelatedType(notification.getRelatedType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreateTime(notification.getCreateTime());

        if ("COLLABORATION_INVITE".equals(notification.getType()) && notification.getRelatedId() != null) {
            dto.setTitle("协作邀请");
            dto.setInviteId(notification.getRelatedId());

            CollaborationInvite invite = inviteRepository.findById(notification.getRelatedId()).orElse(null);
            if (invite != null) {
                dto.setInviteStatus(invite.getStatus());
                User inviter = userRepository.findById(invite.getInviterId()).orElse(null);
                if (inviter != null) {
                    dto.setFromNickname(inviter.getNickname() != null ? inviter.getNickname() : inviter.getUsername());
                }
            } else {
                dto.setInviteStatus("EXPIRED");
            }
        }

        return dto;
    }
}
