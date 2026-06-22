package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.SlideshowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/slideshows")
public class SlideshowController {

    @Autowired
    private SlideshowService slideshowService;

    @PostMapping
    public ResponseEntity<ApiResponse<SlideshowDTO>> create(@RequestBody SlideshowCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(slideshowService.createSlideshow(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SlideshowDTO>> update(@PathVariable Long id, @RequestBody SlideshowUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(slideshowService.updateSlideshow(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            slideshowService.deleteSlideshow(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<SlideshowDTO>>> listMySlideshows() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(slideshowService.listMySlideshows(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SlideshowDTO>> getSlideshow(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(slideshowService.getSlideshow(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<SlideshowDTO>> getSlideshowForEdit(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(slideshowService.getSlideshowForEdit(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<ApiResponse<SlideshowDTO>> getSlideshowByShareToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(ApiResponse.success(slideshowService.getSlideshowByShareToken(token)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<SlideshowDTO>> publish(@PathVariable Long id, @RequestParam boolean isPublic) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("发布成功", slideshowService.publishSlideshow(id, isPublic, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<SlideshowDTO>> unpublish(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("已取消发布", slideshowService.unpublishSlideshow(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{slideshowId}/items")
    public ResponseEntity<ApiResponse<SlideshowItemDTO>> addItem(
            @PathVariable Long slideshowId,
            @RequestBody SlideshowItemAddRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("添加成功", slideshowService.addSlideshowItem(slideshowId, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<SlideshowItemDTO>> updateItem(
            @PathVariable Long itemId,
            @RequestBody SlideshowItemUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("更新成功", slideshowService.updateSlideshowItem(itemId, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long itemId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            slideshowService.removeSlideshowItem(itemId, userId);
            return ResponseEntity.ok(ApiResponse.success("移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{slideshowId}/items")
    public ResponseEntity<ApiResponse<List<SlideshowItemDTO>>> listItems(@PathVariable Long slideshowId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(slideshowService.listSlideshowItems(slideshowId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{slideshowId}/items/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderItems(
            @PathVariable Long slideshowId,
            @RequestBody List<Long> itemIds) {
        try {
            Long userId = UserContext.getCurrentUserId();
            slideshowService.reorderItems(slideshowId, itemIds, userId);
            return ResponseEntity.ok(ApiResponse.success("排序成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/export")
    public ResponseEntity<ApiResponse<String>> exportVideo(
            @PathVariable Long id,
            @RequestBody SlideshowExportRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            SlideshowDTO slideshow = slideshowService.getSlideshowForEdit(id, userId);
            String exportId = "export_" + System.currentTimeMillis() + "_" + id;
            return ResponseEntity.ok(ApiResponse.success("导出任务已创建", exportId));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/export/{exportId}/progress")
    public ResponseEntity<ApiResponse<Integer>> getExportProgress(@PathVariable String exportId) {
        return ResponseEntity.ok(ApiResponse.success(100));
    }
}
