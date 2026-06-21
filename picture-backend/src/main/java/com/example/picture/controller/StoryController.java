package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoryDTO>> create(@RequestBody StoryCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(storyService.createStory(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StoryDTO>> update(@PathVariable Long id, @RequestBody StoryUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(storyService.updateStory(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            storyService.deleteStory(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<StoryDTO>>> listMyStories() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(storyService.listMyStories(userId)));
    }

    @GetMapping("/my/status/{status}")
    public ResponseEntity<ApiResponse<List<StoryDTO>>> listMyStoriesByStatus(@PathVariable Integer status) {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(storyService.listMyStoriesByStatus(userId, status)));
    }

    @GetMapping("/my/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyStoryStats() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(storyService.getMyStoryStats(userId)));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<StoryDTO>>> listPublicStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(storyService.listPublicStories(page, size)));
    }

    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse<List<StoryDTO>>> searchPublicStories(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(storyService.searchPublicStories(keyword, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoryDTO>> getStory(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(storyService.getStory(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<StoryDTO>> getStoryForEdit(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(storyService.getStoryForEdit(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<StoryDTO>> publish(@PathVariable Long id, @RequestParam boolean isPublic) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("发布成功", storyService.publishStory(id, isPublic, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<StoryDTO>> unpublish(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("已取消发布", storyService.unpublishStory(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{storyId}/items")
    public ResponseEntity<ApiResponse<StoryItemDTO>> addItem(
            @PathVariable Long storyId,
            @RequestBody StoryItemAddRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("添加成功", storyService.addStoryItem(storyId, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<StoryItemDTO>> updateItem(
            @PathVariable Long itemId,
            @RequestBody StoryItemUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success("更新成功", storyService.updateStoryItem(itemId, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable Long itemId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            storyService.removeStoryItem(itemId, userId);
            return ResponseEntity.ok(ApiResponse.success("移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{storyId}/items")
    public ResponseEntity<ApiResponse<List<StoryItemDTO>>> listItems(@PathVariable Long storyId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(storyService.listStoryItems(storyId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{storyId}/items/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderItems(
            @PathVariable Long storyId,
            @RequestBody List<Long> itemIds) {
        try {
            Long userId = UserContext.getCurrentUserId();
            storyService.reorderItems(storyId, itemIds, userId);
            return ResponseEntity.ok(ApiResponse.success("排序成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
