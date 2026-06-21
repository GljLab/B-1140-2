package com.example.picture.dto;

import lombok.Data;

@Data
public class AnnotationVersionCreateRequest {
    private String versionName;
    private String description;
}
