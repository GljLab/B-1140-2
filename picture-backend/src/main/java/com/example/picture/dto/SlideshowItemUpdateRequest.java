package com.example.picture.dto;

import lombok.Data;

@Data
public class SlideshowItemUpdateRequest {
    private String caption;
    private String effect;
    private Double effectIntensity;
    private String textAnimation;
    private String textStyle;
    private String textPosition;
    private Integer sortOrder;
    private Integer customInterval;
}
