package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CommentDTO {
    private Long id;
    private Long userId;
    private String nickname;
    private Long pictureId;
    private String content;
    private Date createTime;
}
