package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class RenameResultDTO {
    private Integer successCount;
    private Integer failCount;
    private List<RenamePreviewItem> results;
    private String undoToken;
}
