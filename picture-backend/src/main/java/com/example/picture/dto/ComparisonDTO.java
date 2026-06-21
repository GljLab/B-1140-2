package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ComparisonDTO {
    private Long id;
    private Long userId;
    private String title;
    private String mode;
    private List<Long> pictureIds;
    private List<PictureDTO> pictures;
    private String viewConfig;
    private String thumbnailUrl;
    private Boolean isPublic;
    private Date createTime;
    private Date updateTime;
    private List<ComparisonAnnotationDTO> annotations;
    private String creatorNickname;
}
