package com.example.picture.dto;

import lombok.Data;

import java.util.List;

@Data
public class NoteStatsDTO {
    private Long totalNotes;
    private Long totalWords;
    private Long draftCount;
    private Long publishedCount;
    private List<PictureNoteDTO> recentNotes;
    private List<PictureNoteDTO> topNotesByWords;
}
