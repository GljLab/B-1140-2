package com.example.picture.dto;

import lombok.Data;

import java.util.List;

@Data
public class SlideshowCreateRequest {
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
    private List<Long> pictureIds;
    private Long albumId;
}
