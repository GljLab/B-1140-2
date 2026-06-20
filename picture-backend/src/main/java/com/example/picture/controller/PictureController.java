package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<PictureDTO>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "albumIds", required = false) Long[] albumIds,
            @RequestParam(value = "tags", required = false) String[] tags) throws IOException {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<Long> albumIdList = albumIds != null ? Arrays.asList(albumIds) : null;
            List<String> tagList = tags != null ? Arrays.asList(tags) : null;
            return ResponseEntity.ok(ApiResponse.success(pictureService.upload(file, albumIdList, tagList, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> list(
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "tagId", required = false) Long tagId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Long userId = UserContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(pictureService.listPictures(albumId, tagId, keyword, userId)));
    }

    @GetMapping("/pictures/{id}")
    public ResponseEntity<ApiResponse<PictureDTO>> get(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(pictureService.getPicture(id, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/pictures/{id}")
    public ResponseEntity<ApiResponse<PictureDTO>> update(@PathVariable Long id, @RequestBody PictureUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(pictureService.updatePicture(id, request, userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.deletePicture(id, userId);
            return ResponseEntity.ok(ApiResponse.success("图片将移入回收站，30天内可恢复", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/batch-tags")
    public ResponseEntity<ApiResponse<Void>> batchAddTags(@RequestBody BatchTagRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.batchAddTags(request, userId);
            return ResponseEntity.ok(ApiResponse.success("批量添加成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/batch-album")
    public ResponseEntity<ApiResponse<Void>> batchAddAlbum(@RequestBody BatchAlbumRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.batchAddToAlbum(request, userId);
            return ResponseEntity.ok(ApiResponse.success("批量添加成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/pictures/batch-delete")
    public ResponseEntity<ApiResponse<Void>> batchDelete(@RequestBody BatchDeleteRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.batchDelete(request, userId);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功，图片已移入回收站", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/recycle")
    public ResponseEntity<ApiResponse<List<PictureDTO>>> listRecycleBin() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(pictureService.listRecycleBin(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/recycle/count")
    public ResponseEntity<ApiResponse<Long>> getRecycleBinCount() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(pictureService.countRecycleBin(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/recycle/restore/{id}")
    public ResponseEntity<ApiResponse<Void>> restorePicture(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.restorePicture(id, userId);
            return ResponseEntity.ok(ApiResponse.success("恢复成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/recycle/batch-restore")
    public ResponseEntity<ApiResponse<Void>> batchRestore(@RequestBody BatchDeleteRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.batchRestore(request, userId);
            return ResponseEntity.ok(ApiResponse.success("批量恢复成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/recycle/{id}")
    public ResponseEntity<ApiResponse<Void>> permanentDeletePicture(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.permanentDeletePicture(id, userId);
            return ResponseEntity.ok(ApiResponse.success("永久删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/recycle/batch-delete")
    public ResponseEntity<ApiResponse<Void>> batchPermanentDelete(@RequestBody BatchDeleteRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.batchPermanentDelete(request, userId);
            return ResponseEntity.ok(ApiResponse.success("批量永久删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/recycle/clear")
    public ResponseEntity<ApiResponse<Void>> clearRecycleBin() {
        try {
            Long userId = UserContext.getCurrentUserId();
            pictureService.clearRecycleBin(userId);
            return ResponseEntity.ok(ApiResponse.success("回收站已清空", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
