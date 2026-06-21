package com.example.picture.dto;

import lombok.Data;

@Data
public class StoryItemAddRequest {
    private Long pictureId;
    private String content;
    private Integer sortOrder;
}
