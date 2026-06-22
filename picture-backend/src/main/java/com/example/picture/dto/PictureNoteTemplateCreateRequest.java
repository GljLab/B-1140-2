package com.example.picture.dto;

import lombok.Data;

@Data
public class PictureNoteTemplateCreateRequest {
    private String name;
    private String description;
    private String content;
    private String category;
}
