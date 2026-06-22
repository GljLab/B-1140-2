package com.example.picture.dto;

import lombok.Data;

@Data
public class RenamePreviewItem {
    private Long pictureId;
    private String oldName;
    private String newName;
    private Boolean hasConflict;
    private String conflictReason;
}
