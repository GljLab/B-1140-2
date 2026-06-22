package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PictureNoteService {

    @Autowired
    private PictureNoteRepository noteRepository;

    @Autowired
    private PictureNoteTemplateRepository templateRepository;

    @Autowired
    private PictureNoteReferenceRepository referenceRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserRepository userRepository;

    public PictureNoteDTO getNoteByPictureId(Long pictureId, Long userId) {
        PictureNote note = noteRepository.findByPictureIdAndUserId(pictureId, userId).orElse(null);
        if (note == null) {
            return null;
        }
        return toNoteDTO(note, true);
    }

    public PictureNoteDTO getNoteById(Long id, Long userId) {
        PictureNote note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("笔记不存在"));
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此笔记");
        }
        return toNoteDTO(note, true);
    }

    @Transactional
    public PictureNoteDTO createOrUpdateNote(Long pictureId, PictureNoteUpdateRequest request, Long userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在或无权限");
        }

        PictureNote note = noteRepository.findByPictureIdAndUserId(pictureId, userId).orElse(null);
        if (note == null) {
            note = new PictureNote();
            note.setPictureId(pictureId);
            note.setUserId(userId);
        }

        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
            note.setWordCount(calculateWordCount(request.getContent()));
        }
        if (request.getIsDraft() != null) {
            note.setIsDraft(request.getIsDraft());
        }

        PictureNote saved = noteRepository.save(note);
        return toNoteDTO(saved, true);
    }

    @Transactional
    public void deleteNote(Long id, Long userId) {
        PictureNote note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("笔记不存在"));
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此笔记");
        }
        referenceRepository.deleteByNoteId(id);
        noteRepository.delete(note);
    }

    public List<PictureNoteDTO> listMyNotes(Long userId) {
        List<PictureNote> notes = noteRepository.findByUserIdOrderByUpdateTimeDesc(userId);
        return notes.stream().map(n -> toNoteDTO(n, false)).collect(Collectors.toList());
    }

    public List<PictureNoteDTO> searchMyNotes(String keyword, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<PictureNote> notes;
        if (keyword == null || keyword.trim().isEmpty()) {
            notes = noteRepository.findRecentByUserId(userId, pageable);
        } else {
            notes = noteRepository.searchByUserIdAndKeyword(userId, keyword.trim(), pageable);
        }
        return notes.stream().map(n -> toNoteDTO(n, false)).collect(Collectors.toList());
    }

    public NoteStatsDTO getNoteStats(Long userId) {
        NoteStatsDTO stats = new NoteStatsDTO();
        stats.setTotalNotes(noteRepository.countByUserId(userId));
        stats.setTotalWords(noteRepository.sumWordCountByUserId(userId));
        stats.setDraftCount(noteRepository.countByUserIdAndIsDraft(userId, true));
        stats.setPublishedCount(noteRepository.countByUserIdAndIsDraft(userId, false));

        Pageable pageable = PageRequest.of(0, 5);
        List<PictureNote> recentNotes = noteRepository.findRecentByUserId(userId, pageable);
        stats.setRecentNotes(recentNotes.stream().map(n -> toNoteDTO(n, false)).collect(Collectors.toList()));

        List<PictureNote> topNotes = noteRepository.findTopByWordCount(userId, pageable);
        stats.setTopNotesByWords(topNotes.stream().map(n -> toNoteDTO(n, false)).collect(Collectors.toList()));

        return stats;
    }

    public List<PictureNoteTemplateDTO> listTemplates(Long userId) {
        List<PictureNoteTemplate> templates = templateRepository.findByUserIdOrUserIdIsNullOrderBySortOrderAsc(userId);
        return templates.stream().map(this::toTemplateDTO).collect(Collectors.toList());
    }

    public List<PictureNoteTemplateDTO> listTemplatesByCategory(String category, Long userId) {
        List<PictureNoteTemplate> templates;
        if (userId == null) {
            templates = templateRepository.findByCategoryAndUserIdIsNullOrderBySortOrderAsc(category);
        } else {
            templates = templateRepository.findByCategoryAndUserIdOrderBySortOrderAsc(category, userId);
            List<PictureNoteTemplate> systemTemplates = templateRepository.findByCategoryAndUserIdIsNullOrderBySortOrderAsc(category);
            Set<Long> userTemplateIds = templates.stream().map(PictureNoteTemplate::getId).collect(Collectors.toSet());
            for (PictureNoteTemplate sysTpl : systemTemplates) {
                if (!userTemplateIds.contains(sysTpl.getId())) {
                    templates.add(sysTpl);
                }
            }
            templates.sort(Comparator.comparing(PictureNoteTemplate::getSortOrder));
        }
        return templates.stream().map(this::toTemplateDTO).collect(Collectors.toList());
    }

    @Transactional
    public PictureNoteTemplateDTO createTemplate(PictureNoteTemplateCreateRequest request, Long userId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }

        PictureNoteTemplate template = new PictureNoteTemplate();
        template.setUserId(userId);
        template.setName(request.getName().trim());
        template.setDescription(request.getDescription());
        template.setContent(request.getContent());
        template.setCategory(request.getCategory() != null ? request.getCategory() : "custom");
        template.setSortOrder(100);

        PictureNoteTemplate saved = templateRepository.save(template);
        return toTemplateDTO(saved);
    }

    @Transactional
    public PictureNoteTemplateDTO updateTemplate(Long id, PictureNoteTemplateCreateRequest request, Long userId) {
        PictureNoteTemplate template = templateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("模板不存在或无权限"));

        if (request.getName() != null) {
            template.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            template.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            template.setCategory(request.getCategory());
        }

        PictureNoteTemplate saved = templateRepository.save(template);
        return toTemplateDTO(saved);
    }

    @Transactional
    public void deleteTemplate(Long id, Long userId) {
        PictureNoteTemplate template = templateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("模板不存在或无权限"));
        templateRepository.delete(template);
    }

    @Transactional
    public PictureNoteReferenceDTO addReference(Long noteId, Long pictureId, Long userId) {
        PictureNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("笔记不存在"));
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此笔记");
        }

        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (picture == null || !picture.getUserId().equals(userId)) {
            throw new RuntimeException("图片不存在或无权限");
        }

        if (referenceRepository.existsByNoteIdAndPictureId(noteId, pictureId)) {
            throw new RuntimeException("该图片已关联");
        }

        PictureNoteReference reference = new PictureNoteReference();
        reference.setNoteId(noteId);
        reference.setPictureId(pictureId);
        reference.setUserId(userId);
        reference.setSortOrder(0);

        PictureNoteReference saved = referenceRepository.save(reference);
        return toReferenceDTO(saved);
    }

    @Transactional
    public void removeReference(Long referenceId, Long userId) {
        PictureNoteReference reference = referenceRepository.findById(referenceId)
                .orElseThrow(() -> new RuntimeException("关联不存在"));
        if (!reference.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        referenceRepository.delete(reference);
    }

    public List<PictureNoteReferenceDTO> listReferences(Long noteId, Long userId) {
        PictureNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("笔记不存在"));
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看");
        }

        List<PictureNoteReference> references = referenceRepository.findByNoteIdOrderBySortOrderAscCreateTimeAsc(noteId);
        return references.stream().map(this::toReferenceDTO).collect(Collectors.toList());
    }

    public String exportNoteToMarkdown(Long noteId, Long userId, boolean includeImages) {
        PictureNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("笔记不存在"));
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("无权导出");
        }

        StringBuilder sb = new StringBuilder();
        if (note.getTitle() != null && !note.getTitle().isEmpty()) {
            sb.append("# ").append(note.getTitle()).append("\n\n");
        }

        Picture picture = pictureRepository.findById(note.getPictureId()).orElse(null);
        if (picture != null) {
            sb.append("![").append(picture.getName() != null ? picture.getName() : "图片").append("\n\n");
            if (includeImages && picture.getUrl() != null) {
                sb.append("![](").append(picture.getUrl()).append(")\n\n");
            }
        }

        if (note.getContent() != null) {
            sb.append(note.getContent()).append("\n\n");
        }

        List<PictureNoteReference> references = referenceRepository.findByNoteIdOrderBySortOrderAscCreateTimeAsc(noteId);
        if (!references.isEmpty()) {
            sb.append("---\n\n");
            sb.append("## 关联图片\n\n");
            for (PictureNoteReference ref : references) {
                Picture refPic = pictureRepository.findById(ref.getPictureId()).orElse(null);
                if (refPic != null) {
                    sb.append("- ").append(refPic.getName() != null ? refPic.getName() : "图片");
                    if (includeImages && refPic.getUrl() != null) {
                        sb.append("\n  ![](").append(refPic.getUrl()).append(")");
                    }
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    public String exportNotesToMarkdown(List<Long> noteIds, Long userId, boolean includeImages) {
        if (noteIds == null || noteIds.isEmpty()) {
            List<PictureNote> allNotes = noteRepository.findByUserIdOrderByUpdateTimeDesc(userId);
            noteIds = allNotes.stream().map(PictureNote::getId).collect(Collectors.toList());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("# 图片笔记集\n\n");
        sb.append("导出时间: ").append(new Date()).append("\n\n");
        sb.append("共 ").append(noteIds.size()).append(" 篇笔记\n\n");

        sb.append("## 目录\n\n");
        int index = 1;
        for (Long noteId : noteIds) {
            PictureNote note = noteRepository.findById(noteId).orElse(null);
            if (note != null && note.getUserId().equals(userId)) {
                String title = note.getTitle() != null && !note.getTitle().isEmpty() ? note.getTitle() : "未命名笔记";
                sb.append(index).append(". [").append(title).append("](#note-").append(index).append(")\n");
                index++;
            }
        }
        sb.append("\n---\n\n");

        index = 1;
        for (Long noteId : noteIds) {
            try {
                String noteContent = exportNoteToMarkdown(noteId, userId, includeImages);
                sb.append("<a id=\"note-").append(index).append("\"></a>\n\n");
                sb.append(noteContent);
                sb.append("\n---\n\n");
                index++;
            } catch (Exception e) {
                    continue;
                }
            }

        return sb.toString();
    }

    private int calculateWordCount(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        String text = content.replaceAll("[#*_`>\\-\\[\\]()!\\n]", "");
        return text.length();
    }

    private PictureNoteDTO toNoteDTO(PictureNote note, boolean includeReferences) {
        PictureNoteDTO dto = new PictureNoteDTO();
        dto.setId(note.getId());
        dto.setPictureId(note.getPictureId());
        dto.setUserId(note.getUserId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setWordCount(note.getWordCount());
        dto.setIsDraft(note.getIsDraft());
        dto.setCreateTime(note.getCreateTime());
        dto.setUpdateTime(note.getUpdateTime());

        Picture picture = pictureRepository.findById(note.getPictureId()).orElse(null);
        if (picture != null) {
            dto.setPictureUrl(picture.getUrl());
            dto.setPictureName(picture.getName());
        }

        if (includeReferences) {
            List<PictureNoteReference> references = referenceRepository.findByNoteIdOrderBySortOrderAscCreateTimeAsc(note.getId());
            dto.setReferences(references.stream().map(this::toReferenceDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private PictureNoteTemplateDTO toTemplateDTO(PictureNoteTemplate template) {
        PictureNoteTemplateDTO dto = new PictureNoteTemplateDTO();
        dto.setId(template.getId());
        dto.setUserId(template.getUserId());
        dto.setName(template.getName());
        dto.setDescription(template.getDescription());
        dto.setContent(template.getContent());
        dto.setCategory(template.getCategory());
        dto.setSortOrder(template.getSortOrder());
        dto.setCreateTime(template.getCreateTime());
        dto.setUpdateTime(template.getUpdateTime());
        dto.setIsSystem(template.getUserId() == null);
        return dto;
    }

    private PictureNoteReferenceDTO toReferenceDTO(PictureNoteReference reference) {
        PictureNoteReferenceDTO dto = new PictureNoteReferenceDTO();
        dto.setId(reference.getId());
        dto.setNoteId(reference.getNoteId());
        dto.setPictureId(reference.getPictureId());
        dto.setSortOrder(reference.getSortOrder());
        dto.setCreateTime(reference.getCreateTime());

        Picture picture = pictureRepository.findById(reference.getPictureId()).orElse(null);
        if (picture != null) {
            dto.setPictureUrl(picture.getUrl());
            dto.setPictureName(picture.getName());
        }

        return dto;
    }
}
