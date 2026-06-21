package com.example.picture.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WatermarkTemplateDTO {
    private Long id;
    private String name;
    private Long userId;
    private Boolean isSystem;
    private String type;
    private String textContent;
    private String fontFamily;
    private Integer fontSize;
    private String fontColor;
    private Double opacity;
    private String imageUrl;
    private Integer imageWidth;
    private Integer imageHeight;
    private String position;
    private Integer marginX;
    private Integer marginY;
    private Double tileGap;
    private Boolean smartMode;
    private Date createTime;
    private Date updateTime;
}
