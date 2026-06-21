package com.example.picture.dto;

import lombok.Data;

@Data
public class AnnotationCommentCreateRequest {
    private Long parentId;
    private String content;
}
