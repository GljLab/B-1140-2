package com.example.picture.dto;

import lombok.Data;

@Data
public class AnnotationReviewCreateRequest {
    private Long pictureId;
    private Long reviewerId;
    private String comment;
}
