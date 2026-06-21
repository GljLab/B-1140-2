package com.example.picture.dto;

import lombok.Data;

@Data
public class AnnotationPermissionRequest {
    private Long userId;
    private Boolean canAnnotate;
    private Boolean canView;
    private Boolean isPublicAnnotations;
}
