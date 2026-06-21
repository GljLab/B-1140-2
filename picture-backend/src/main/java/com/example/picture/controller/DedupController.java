package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.DedupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dedup")
public class DedupController {

    @Autowired
    private DedupService dedupService;

    @GetMapping("/scan")
    public ResponseEntity<ApiResponse<DedupResultDTO>> scanDuplicates(
            @RequestParam(value = "similarityLevel", defaultValue = "MEDIUM") String similarityLevel) {
        try {
            Long userId = UserContext.getCurrentUserId();
            DedupResultDTO result = dedupService.detectDuplicates(userId, similarityLevel);
            return ResponseEntity.ok(ApiResponse.success("扫描完成", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("扫描失败: " + e.getMessage()));
        }
    }

    @PostMapping("/apply-recommendations")
    public ResponseEntity<ApiResponse<Integer>> applyRecommendations(@RequestBody DedupProcessRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            int count = dedupService.applyRecommendations(request, userId);
            return ResponseEntity.ok(ApiResponse.success("已按推荐删除 " + count + " 张重复图片", count));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("处理失败: " + e.getMessage()));
        }
    }

    @PostMapping("/process-selections")
    public ResponseEntity<ApiResponse<Integer>> processSelections(@RequestBody DedupProcessRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            int count = dedupService.processSelections(request, userId);
            return ResponseEntity.ok(ApiResponse.success("已删除 " + count + " 张图片", count));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("处理失败: " + e.getMessage()));
        }
    }

    @PostMapping("/batch-delete")
    public ResponseEntity<ApiResponse<Integer>> batchDelete(@RequestBody BatchDeleteRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            int count = dedupService.batchDeletePictures(request.getPictureIds(), userId);
            return ResponseEntity.ok(ApiResponse.success("已删除 " + count + " 张图片", count));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    @GetMapping("/whitelist")
    public ResponseEntity<ApiResponse<List<WhitelistDTO>>> getWhitelist() {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<WhitelistDTO> list = dedupService.getWhitelist(userId);
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取白名单失败: " + e.getMessage()));
        }
    }

    @PostMapping("/whitelist/add")
    public ResponseEntity<ApiResponse<Void>> addToWhitelist(@RequestBody WhitelistAddRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            dedupService.addToWhitelist(request, userId);
            return ResponseEntity.ok(ApiResponse.success("已添加到白名单", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("添加失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/whitelist/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFromWhitelist(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            dedupService.removeFromWhitelist(id, userId);
            return ResponseEntity.ok(ApiResponse.success("已从白名单移除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("移除失败: " + e.getMessage()));
        }
    }
}
