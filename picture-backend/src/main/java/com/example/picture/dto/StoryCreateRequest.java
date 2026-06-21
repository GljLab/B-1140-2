package com.example.picture.dto;

import lombok.Data;

@Data
public class StoryCreateRequest {
    private String title;
    private String description;
    private String layout;
}
