package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.PrivateSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private-space")
public class PrivateSpaceController {

    @Autowired
    private PrivateSpaceService privateSpaceService;

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> getConfig() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(privateSpaceService.getConfig(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/password/set")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> setPassword(@RequestBody PrivatePasswordSetRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("密码设置成功", privateSpaceService.setPassword(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/password/verify")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> verifyPassword(@RequestBody PrivatePasswordVerifyRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("验证成功", privateSpaceService.verifyPassword(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/password/change")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> changePassword(@RequestBody PrivatePasswordChangeRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("密码修改成功", privateSpaceService.changePassword(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> resetPassword(
            @RequestParam String accountPassword,
            @RequestParam String newPassword) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("密码重置成功",
                    privateSpaceService.resetPassword(userId, accountPassword, newPassword)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/security-question/set")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> setSecurityQuestion(@RequestBody SecurityQuestionSetRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("安全问题设置成功", privateSpaceService.setSecurityQuestion(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/security-question/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifySecurityQuestion(@RequestBody SecurityQuestionVerifyRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean result = privateSpaceService.verifySecurityQuestion(userId, request);
            return ResponseEntity.ok(ApiResponse.success(result ? "验证成功" : "验证失败", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<PrivateSpaceConfigDTO>> updateSettings(@RequestBody PrivateSpaceSettingsRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("设置更新成功", privateSpaceService.updateSettings(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<Void>> lockPrivateSpace() {
        try {
            Long userId = UserContext.getCurrentUserId();
            privateSpaceService.lockPrivateSpace(userId);
            return ResponseEntity.ok(ApiResponse.success("私密空间已锁定", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check-lock")
    public ResponseEntity<ApiResponse<Boolean>> checkAutoLock() {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean locked = privateSpaceService.checkAutoLock(userId);
            return ResponseEntity.ok(ApiResponse.success(locked ? "已锁定" : "未锁定", locked));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> listPrivatePictures() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(privateSpaceService.listPrivatePictures(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pictures/{id}")
    public ResponseEntity<ApiResponse<PictureDTO>> getPrivatePicture(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(privateSpaceService.getPrivatePicture(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countPrivatePictures() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(privateSpaceService.countPrivatePictures(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/{id}/move-in")
    public ResponseEntity<ApiResponse<PictureDTO>> moveToPrivate(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("已移入私密空间", privateSpaceService.moveToPrivate(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/{id}/move-out")
    public ResponseEntity<ApiResponse<PictureDTO>> moveFromPrivate(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("已移出私密空间", privateSpaceService.moveFromPrivate(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/batch-move-in")
    public ResponseEntity<ApiResponse<Integer>> batchMoveToPrivate(@RequestBody BatchMovePrivateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            int count = privateSpaceService.batchMoveToPrivate(request.getPictureIds(), userId);
            return ResponseEntity.ok(ApiResponse.success("成功移入 " + count + " 张图片到私密空间", count));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/batch-move-out")
    public ResponseEntity<ApiResponse<Integer>> batchMoveFromPrivate(@RequestBody BatchMovePrivateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            int count = privateSpaceService.batchMoveFromPrivate(request.getPictureIds(), userId);
            return ResponseEntity.ok(ApiResponse.success("成功移出 " + count + " 张图片", count));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
