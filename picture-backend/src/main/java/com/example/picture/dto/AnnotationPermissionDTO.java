package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AnnotationPermissionDTO {
    private Long id;
    private Long pictureId;
    private Long userId;
    private String nickname;
    private Boolean canAnnotate;
    private Boolean canView;
    private Boolean isPublicAnnotations;
    private Date createTime;
    private Date updateTime;
}
