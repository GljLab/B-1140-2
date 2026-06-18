package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.AlbumService;
import com.example.picture.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PictureService pictureService;

    @PostMapping
    public ResponseEntity<ApiResponse<AlbumDTO>> create(@RequestBody AlbumCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(albumService.createAlbum(request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AlbumDTO>> update(@PathVariable Long id, @RequestBody AlbumUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(albumService.updateAlbum(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @RequestParam(defaultValue = "false") boolean deletePictures) {
        try {
            Long userId = UserContext.getCurrentUserId();
            albumService.deleteAlbum(id, deletePictures, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> list() {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(albumService.listAlbums(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlbumDTO>> get(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(albumService.getAlbum(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/pictures")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> getPictures(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(pictureService.listPictures(id, null, null, userId)));
    }

    @PostMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorder(@RequestBody Map<Long, Integer> orderMap) {
        Long userId = UserContext.getCurrentUserId();
        albumService.reorderAlbums(orderMap, userId);
        return ResponseEntity.ok(ApiResponse.success("排序成功", null));
    }

    @PostMapping("/{albumId}/pictures/{pictureId}")
    public ResponseEntity<ApiResponse<Void>> addPicture(@PathVariable Long albumId, @PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            albumService.addPictureToAlbum(albumId, pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success("添加成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{albumId}/pictures/{pictureId}")
    public ResponseEntity<ApiResponse<Void>> removePicture(@PathVariable Long albumId, @PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            albumService.removePictureFromAlbum(albumId, pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success("移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> search(@RequestParam String keyword) {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(albumService.searchAlbums(keyword, userId)));
    }
}
