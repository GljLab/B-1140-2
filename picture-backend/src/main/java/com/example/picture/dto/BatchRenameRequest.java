package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchRenameRequest {
    private List<Long> pictureIds;
    private String template;
    private String customText;
    private Integer indexStart;
    private Integer indexDigits;
    private String findText;
    private String replaceText;
    private Boolean useRegex;
    private String caseTransform;
}
