package com.example.picture.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SlideshowItemDTO {
    private Long id;
    private Long slideshowId;
    private Long pictureId;
    private String pictureUrl;
    private String pictureName;
    private String caption;
    private String effect;
    private Double effectIntensity;
    private String textAnimation;
    private String textStyle;
    private String textPosition;
    private Integer sortOrder;
    private Integer customInterval;
    private Date createTime;
    private Date updateTime;
}
