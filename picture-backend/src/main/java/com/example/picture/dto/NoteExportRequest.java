package com.example.picture.dto;

import lombok.Data;

import java.util.List;

@Data
public class NoteExportRequest {
    private List<Long> noteIds;
    private Boolean includeImages;
}
