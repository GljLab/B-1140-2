package com.example.picture.dto;

import lombok.Data;

@Data
public class PictureNoteUpdateRequest {
    private String title;
    private String content;
    private Boolean isDraft;
}
