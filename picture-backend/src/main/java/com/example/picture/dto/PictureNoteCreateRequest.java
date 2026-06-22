package com.example.picture.dto;

import lombok.Data;

@Data
public class PictureNoteCreateRequest {
    private Long pictureId;
    private String title;
    private String content;
}
