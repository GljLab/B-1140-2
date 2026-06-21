package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class AnnotationCommentDTO {
    private Long id;
    private Long annotationId;
    private Long userId;
    private String nickname;
    private Long parentId;
    private String parentUserNickname;
    private String content;
    private Date createTime;
    private List<AnnotationCommentDTO> comments;
}
