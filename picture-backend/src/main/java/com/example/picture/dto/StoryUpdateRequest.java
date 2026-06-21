package com.example.picture.dto;

import lombok.Data;

@Data
public class StoryUpdateRequest {
    private String title;
    private String description;
    private String layout;
    private String coverUrl;
    private Boolean isPublic;
}
