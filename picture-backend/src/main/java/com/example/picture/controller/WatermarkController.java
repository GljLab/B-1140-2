package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.entity.WatermarkConfig;
import com.example.picture.entity.WatermarkTemplate;
import com.example.picture.service.WatermarkService;
import com.example.picture.util.WatermarkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watermark")
public class WatermarkController {

    @Autowired
    private WatermarkService watermarkService;

    @Value("${upload.path:/app/images/}")
    private String uploadPath;

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<WatermarkConfigDTO>> getConfig() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.getConfig(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/config")
    public ResponseEntity<ApiResponse<WatermarkConfigDTO>> saveConfig(@RequestBody WatermarkConfigSaveRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.saveConfig(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<WatermarkTemplateDTO>>> listTemplates() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.listTemplates(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/templates/mine")
    public ResponseEntity<ApiResponse<List<WatermarkTemplateDTO>>> listMyTemplates() {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.listMyTemplates(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<WatermarkTemplateDTO>> createTemplate(@RequestBody WatermarkTemplateSaveRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.createTemplate(userId, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<WatermarkTemplateDTO>> updateTemplate(@PathVariable Long id, @RequestBody WatermarkTemplateSaveRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            return ResponseEntity.ok(ApiResponse.success(watermarkService.updateTemplate(userId, id, request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            watermarkService.deleteTemplate(userId, id);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/upload-watermark-image")
    public ResponseEntity<ApiResponse<String>> uploadWatermarkImage(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            Long userId = UserContext.getCurrentUserId();
            String originalName = file.getOriginalFilename();
            String suffix = "";
            if (originalName != null && originalName.contains(".")) {
                suffix = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = "wm_" + userId + "_" + UUID.randomUUID().toString() + suffix;
            Path path = Paths.get(uploadPath + fileName);
            Files.write(path, file.getBytes());
            return ResponseEntity.ok(ApiResponse.success("/images/" + fileName));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<String>> previewWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "templateId", required = false) Long templateId) throws IOException {
        try {
            Long userId = UserContext.getCurrentUserId();

            String originalName = file.getOriginalFilename();
            String suffix = "";
            if (originalName != null && originalName.contains(".")) {
                suffix = originalName.substring(originalName.lastIndexOf("."));
            }
            String originalFileName = "preview_orig_" + userId + "_" + UUID.randomUUID().toString() + suffix;
            Path originalPath = Paths.get(uploadPath + originalFileName);
            Files.write(originalPath, file.getBytes());

            String previewFileName = "preview_wm_" + userId + "_" + UUID.randomUUID().toString() + suffix;
            File previewFile = new File(uploadPath + previewFileName);

            if (templateId != null) {
                WatermarkTemplate template = watermarkService.getTemplateEntity(templateId, userId);
                if (template != null) {
                    WatermarkConfig config = watermarkService.templateToConfig(template, userId);
                    WatermarkUtil.addWatermark(originalPath.toFile(), previewFile, config, uploadPath);
                } else {
                    Files.copy(originalPath, previewFile.toPath());
                }
            } else {
                WatermarkConfig config = watermarkService.getConfigEntity(userId);
                if (config != null && Boolean.TRUE.equals(config.getEnabled())) {
                    WatermarkUtil.addWatermark(originalPath.toFile(), previewFile, config, uploadPath);
                } else {
                    Files.copy(originalPath, previewFile.toPath());
                }
            }

            return ResponseEntity.ok(ApiResponse.success("/images/" + previewFileName));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
