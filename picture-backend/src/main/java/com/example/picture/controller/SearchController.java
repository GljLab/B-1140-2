package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<PictureDTO>>> search(@RequestBody SearchRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.search(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PictureDTO>>> searchGet(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "searchType", required = false) String searchType) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.searchByType(keyword, searchType, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/content")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> searchByContent(@RequestParam("keyword") String keyword) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.searchByContent(keyword, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/similar/{pictureId}")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> findSimilarPictures(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.findSimilarPictures(pictureId, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<SearchSuggestionDTO>>> getSuggestions(
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.getSuggestions(keyword, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SearchHistoryDTO>>> getSearchHistory() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.getSearchHistory(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> clearSearchHistory() {
        try {
            Long userId = UserContext.getCurrentUserId();
            searchService.clearSearchHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("搜索历史已清空", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<SavedSearchDTO>>> getSavedSearches() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.getSavedSearches(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/saved")
    public ResponseEntity<ApiResponse<SavedSearchDTO>> saveSearch(@RequestBody SaveSearchRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.saveSearch(request.getName(), request.getSearchParams(), userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/saved/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSavedSearch(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            searchService.deleteSavedSearch(id, userId);
            return ResponseEntity.ok(ApiResponse.success("快捷搜索已删除", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/saved/{id}/execute")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> executeSavedSearch(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(searchService.executeSavedSearch(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
