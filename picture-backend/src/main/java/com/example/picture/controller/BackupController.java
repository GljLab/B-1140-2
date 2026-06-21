package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/export/album/{albumId}")
    public ResponseEntity<byte[]> exportAlbum(
            @PathVariable Long albumId,
            @RequestParam(value = "quality", defaultValue = "original") String quality,
            @RequestParam(value = "format", defaultValue = "original") String format) {
        try {
            Long userId = UserContext.getCurrentUserId();
            byte[] zipData = backupService.exportAlbum(albumId, quality, format, userId);

            String fileName = "album_" + albumId + ".zip";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipData.length);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            return ResponseEntity.ok().headers(headers).body(zipData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(("导出失败: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/export/pictures")
    public ResponseEntity<byte[]> exportPictures(
            @RequestBody ExportRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            byte[] zipData = backupService.exportPictures(
                    request.getPictureIds(),
                    request.getQuality(),
                    request.getFormat(),
                    userId);

            String fileName = "pictures_export.zip";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipData.length);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            return ResponseEntity.ok().headers(headers).body(zipData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(("导出失败: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BackupDTO>> createBackup() {
        try {
            Long userId = UserContext.getCurrentUserId();
            BackupDTO backup = backupService.createFullBackup(userId);
            return ResponseEntity.ok(ApiResponse.success("备份已开始创建", backup));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BackupDTO>>> listBackups() {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<BackupDTO> backups = backupService.listBackups(userId);
            return ResponseEntity.ok(ApiResponse.success(backups));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BackupDTO>> getBackup(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            BackupDTO backup = backupService.getBackup(id, userId);
            return ResponseEntity.ok(ApiResponse.success(backup));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadBackup(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            File file = backupService.getBackupFile(id, userId);
            byte[] data = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(data.length);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));

            return ResponseEntity.ok().headers(headers).body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(("下载失败: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBackup(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            backupService.deleteBackup(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<ApiResponse<RestoreResultDTO>> restoreBackup(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "strategy", defaultValue = "skip") String strategy) {
        try {
            Long userId = UserContext.getCurrentUserId();
            RestoreResultDTO result = backupService.restoreBackup(file, strategy, userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
