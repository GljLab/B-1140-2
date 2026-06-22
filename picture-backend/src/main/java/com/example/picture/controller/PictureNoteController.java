package com.example.picture.controller;

import com.example.picture.context.UserContext;
import com.example.picture.dto.*;
import com.example.picture.service.PictureNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class PictureNoteController {

    @Autowired
    private PictureNoteService noteService;

    @GetMapping("/picture/{pictureId}")
    public ResponseEntity<ApiResponse<PictureNoteDTO>> getNoteByPictureId(@PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteDTO note = noteService.getNoteByPictureId(pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success(note));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PictureNoteDTO>> getNoteById(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteDTO note = noteService.getNoteById(id, userId);
            return ResponseEntity.ok(ApiResponse.success(note));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/picture/{pictureId}")
    public ResponseEntity<ApiResponse<PictureNoteDTO>> saveNote(@PathVariable Long pictureId, @RequestBody PictureNoteUpdateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteDTO note = noteService.createOrUpdateNote(pictureId, request, userId);
            return ResponseEntity.ok(ApiResponse.success("保存成功", note));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            noteService.deleteNote(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PictureNoteDTO>>> listMyNotes() {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<PictureNoteDTO> notes = noteService.listMyNotes(userId);
            return ResponseEntity.ok(ApiResponse.success(notes));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PictureNoteDTO>>> searchNotes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<PictureNoteDTO> notes = noteService.searchMyNotes(keyword, userId, page, size);
            return ResponseEntity.ok(ApiResponse.success(notes));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<NoteStatsDTO>> getStats() {
        try {
            Long userId = UserContext.getCurrentUserId();
            NoteStatsDTO stats = noteService.getNoteStats(userId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<PictureNoteTemplateDTO>>> listTemplates() {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<PictureNoteTemplateDTO> templates = noteService.listTemplates(userId);
            return ResponseEntity.ok(ApiResponse.success(templates));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/templates/category/{category}")
    public ResponseEntity<ApiResponse<List<PictureNoteTemplateDTO>>> listTemplatesByCategory(@PathVariable String category) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<PictureNoteTemplateDTO> templates = noteService.listTemplatesByCategory(category, userId);
            return ResponseEntity.ok(ApiResponse.success(templates));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<PictureNoteTemplateDTO>> createTemplate(@RequestBody PictureNoteTemplateCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteTemplateDTO template = noteService.createTemplate(request, userId);
            return ResponseEntity.ok(ApiResponse.success("创建成功", template));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<PictureNoteTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @RequestBody PictureNoteTemplateCreateRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteTemplateDTO template = noteService.updateTemplate(id, request, userId);
            return ResponseEntity.ok(ApiResponse.success("更新成功", template));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        try {
            Long userId = UserContext.getCurrentUserId();
            noteService.deleteTemplate(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{noteId}/references/{pictureId}")
    public ResponseEntity<ApiResponse<PictureNoteReferenceDTO>> addReference(
            @PathVariable Long noteId,
            @PathVariable Long pictureId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            PictureNoteReferenceDTO reference = noteService.addReference(noteId, pictureId, userId);
            return ResponseEntity.ok(ApiResponse.success("添加成功", reference));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/references/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> removeReference(@PathVariable Long referenceId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            noteService.removeReference(referenceId, userId);
            return ResponseEntity.ok(ApiResponse.success("移除成功", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{noteId}/references")
    public ResponseEntity<ApiResponse<List<PictureNoteReferenceDTO>>> listReferences(@PathVariable Long noteId) {
        try {
            Long userId = UserContext.getCurrentUserId();
            List<PictureNoteReferenceDTO> references = noteService.listReferences(noteId, userId);
            return ResponseEntity.ok(ApiResponse.success(references));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportNote(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean includeImages) {
        try {
            Long userId = UserContext.getCurrentUserId();
            String content = noteService.exportNoteToMarkdown(id, userId, includeImages);
            String fileName = "note_" + id + ".md";
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportNotes(@RequestBody NoteExportRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            boolean includeImages = request.getIncludeImages() != null && request.getIncludeImages();
            String content = noteService.exportNotesToMarkdown(request.getNoteIds(), userId, includeImages);
            String fileName = "notes_export.md";
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
