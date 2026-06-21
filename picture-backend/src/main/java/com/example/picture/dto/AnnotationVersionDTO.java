package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AnnotationVersionDTO {
    private Long id;
    private Long pictureId;
    private Long userId;
    private String nickname;
    private String versionName;
    private String description;
    private Integer versionNumber;
    private Date createTime;
    private Integer annotationCount;
}
