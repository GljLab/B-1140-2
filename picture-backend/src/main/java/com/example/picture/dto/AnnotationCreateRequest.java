package com.example.picture.dto;

import lombok.Data;

@Data
public class AnnotationCreateRequest {
    private Long versionId;
    private String toolType;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private String pointsData;
    private String color;
    private Integer strokeWidth;
    private String textContent;
    private Boolean isPublic = true;
}
