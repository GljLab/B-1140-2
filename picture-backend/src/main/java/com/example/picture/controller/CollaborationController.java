package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.CollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collaboration")
public class CollaborationController {

    @Autowired
    private CollaborationService collaborationService;

    @PostMapping("/albums/{albumId}/invite")
    public ResponseEntity<ApiResponse<CollaborationInviteDTO>> inviteCollaborator(
            @PathVariable Long albumId,
            @RequestBody InviteCollaboratorRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(
                    collaborationService.inviteCollaborator(albumId, request.getUsername(), userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<ApiResponse<CollaborationInviteDTO>> acceptInvite(@PathVariable Long inviteId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collaborationService.acceptInvite(inviteId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/invites/{inviteId}/reject")
    public ResponseEntity<ApiResponse<CollaborationInviteDTO>> rejectInvite(@PathVariable Long inviteId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collaborationService.rejectInvite(inviteId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/albums/{albumId}/collaborators")
    public ResponseEntity<ApiResponse<List<CollaboratorDTO>>> getCollaborators(@PathVariable Long albumId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collaborationService.getCollaborators(albumId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/albums/{albumId}/collaborators/{collaboratorUserId}")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
            @PathVariable Long albumId,
            @PathVariable Long collaboratorUserId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            collaborationService.removeCollaborator(albumId, collaboratorUserId, userId);
            return ResponseEntity.ok(ApiResponse.success("移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/albums/{albumId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveCollaboration(@PathVariable Long albumId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            collaborationService.leaveCollaboration(albumId, userId);
            return ResponseEntity.ok(ApiResponse.success("已退出协作", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/invites/pending")
    public ResponseEntity<ApiResponse<List<CollaborationInviteDTO>>> getPendingInvites() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(collaborationService.getPendingInvites(userId)));
    }

    @GetMapping("/albums/{albumId}/permissions")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getPermissions(@PathVariable Long albumId) {
        Long userId = UserContext.getCurrentUserId();
        Map<String, Boolean> permissions = Map.of(
                "canEdit", collaborationService.canEditAlbum(albumId, userId),
                "canDelete", collaborationService.canDeleteAlbum(albumId, userId),
                "canManageCollaborators", collaborationService.canManageCollaborators(albumId, userId)
        );
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }
}
