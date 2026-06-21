package com.example.picture.dto;

import lombok.Data;

@Data
public class StoryItemUpdateRequest {
    private String content;
    private Integer sortOrder;
}
