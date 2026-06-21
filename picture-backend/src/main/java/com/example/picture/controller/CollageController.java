package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.ApiResponse;
import com.example.picture.dto.CollageCreateRequest;
import com.example.picture.dto.CollageDTO;
import com.example.picture.dto.CollageUpdateRequest;
import com.example.picture.service.CollageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collage")
public class CollageController {

    @Autowired
    private CollageService collageService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CollageDTO>>> listCollages() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collageService.listCollages(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollageDTO>> getCollage(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collageService.getCollage(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CollageDTO>> createCollage(@RequestBody CollageCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collageService.createCollage(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CollageDTO>> updateCollage(@PathVariable Long id,
                                                                  @RequestBody CollageUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(collageService.updateCollage(id, userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCollage(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            collageService.deleteCollage(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<String>> generateCollage(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            String url = collageService.generateCollage(id, userId);
            return ResponseEntity.ok(ApiResponse.success(url));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
