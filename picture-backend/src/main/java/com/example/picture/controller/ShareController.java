package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @PostMapping("/shares")
    public ResponseEntity<ApiResponse<ShareDTO>> createShare(@RequestBody ShareCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(shareService.createShare(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shares/{token}")
    public ResponseEntity<ApiResponse<ShareDTO>> getShareInfo(@PathVariable String token) {
        try {
            return ResponseEntity.ok(ApiResponse.success(shareService.getShareInfo(token)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/shares/{token}/access")
    public ResponseEntity<ApiResponse<ShareDTO>> accessShare(
            @PathVariable String token,
            @RequestBody(required = false) ShareAccessRequest request) {
        try {
            String password = request != null ? request.getPassword() : null;
            return ResponseEntity.ok(ApiResponse.success(shareService.accessShare(token, password)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shares")
    public ResponseEntity<ApiResponse<List<ShareDTO>>> getMyShares() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(shareService.getMyShares(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/shares/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShare(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            shareService.deleteShare(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
