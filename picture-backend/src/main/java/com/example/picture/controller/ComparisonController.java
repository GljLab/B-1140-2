package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService;

    @PostMapping("/comparisons")
    public ResponseEntity<ApiResponse<ComparisonDTO>> createComparison(
            @RequestBody ComparisonCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.createComparison(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/comparisons/{id}")
    public ResponseEntity<ApiResponse<ComparisonDTO>> updateComparison(
            @PathVariable Long id,
            @RequestBody ComparisonUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.updateComparison(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/comparisons/{id}")
    public ResponseEntity<ApiResponse<ComparisonDTO>> getComparison(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserIdSafe();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.getComparison(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/comparisons")
    public ResponseEntity<ApiResponse<List<ComparisonDTO>>> listMyComparisons() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.listMyComparisons(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/comparisons/public")
    public ResponseEntity<ApiResponse<List<ComparisonDTO>>> listPublicComparisons() {
        try {
            return ResponseEntity.ok(ApiResponse.success(comparisonService.listPublicComparisons()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/comparisons/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComparison(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            comparisonService.deleteComparison(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/comparisons/annotations")
    public ResponseEntity<ApiResponse<ComparisonAnnotationDTO>> addAnnotation(
            @RequestBody ComparisonAnnotationCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.addAnnotation(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/comparisons/annotations/{id}")
    public ResponseEntity<ApiResponse<ComparisonAnnotationDTO>> updateAnnotation(
            @PathVariable Long id,
            @RequestBody ComparisonAnnotationCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.updateAnnotation(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/comparisons/annotations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnotation(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            comparisonService.deleteAnnotation(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/comparisons/{id}/annotations")
    public ResponseEntity<ApiResponse<List<ComparisonAnnotationDTO>>> listAnnotations(
            @PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserIdSafe();
            return ResponseEntity.ok(ApiResponse.success(comparisonService.listAnnotations(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
