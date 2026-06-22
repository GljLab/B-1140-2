package com.example.picture.dto;

import lombok.Data;

@Data
public class SlideshowUpdateRequest {
    private String title;
    private String description;
    private String transition;
    private Integer interval;
    private Boolean loop;
    private Boolean autoStart;
    private String bgMusicUrl;
    private String bgMusicType;
    private Double bgMusicVolume;
    private Boolean bgMusicLoop;
    private String effect;
    private Double effectIntensity;
    private String titlePageText;
    private String textAnimation;
    private String textStyle;
    private String textPosition;
    private Boolean isPublic;
    private Boolean allowDownload;
}
