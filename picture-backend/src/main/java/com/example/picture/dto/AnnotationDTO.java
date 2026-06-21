package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class AnnotationDTO {
    private Long id;
    private Long pictureId;
    private Long versionId;
    private Long userId;
    private String nickname;
    private String toolType;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private String pointsData;
    private String color;
    private Integer strokeWidth;
    private String textContent;
    private Boolean isResolved;
    private Long resolvedBy;
    private String resolvedByNickname;
    private Date resolvedTime;
    private Boolean isPublic;
    private Date createTime;
    private Date updateTime;
    private List<AnnotationCommentDTO> comments;
    private Integer commentCount;
}
