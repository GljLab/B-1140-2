package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.ApiResponse;
import com.example.picture.dto.NotificationDTO;
import com.example.picture.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotifications(userId)));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadNotifications(userId)));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(userId)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("已标记为已读", null));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long userId = UserContext.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("已全部标记为已读", null));
    }
}
