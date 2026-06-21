package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ComparisonAnnotationDTO {
    private Long id;
    private Long comparisonId;
    private Long pictureId;
    private String toolType;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private String pointsData;
    private String color;
    private Integer strokeWidth;
    private String textContent;
    private Integer fontSize;
    private Date createTime;
    private Date updateTime;
}
