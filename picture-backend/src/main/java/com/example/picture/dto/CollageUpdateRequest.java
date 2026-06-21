package com.example.picture.dto;

import lombok.Data;

import java.util.List;

@Data
public class CollageUpdateRequest {
    private String name;
    private String templateType;
    private Integer canvasWidth;
    private Integer canvasHeight;
    private String backgroundColor;
    private String backgroundImageUrl;
    private Integer gap;
    private Integer borderRadius;
    private Integer borderWidth;
    private String borderColor;
    private String items;
    private String texts;
    private String stickers;
    private String sizePreset;
    private String quality;
    private List<Long> pictureIds;
}
