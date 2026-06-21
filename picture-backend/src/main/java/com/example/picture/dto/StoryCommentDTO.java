package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class StoryCommentDTO {
    private Long id;
    private Long userId;
    private String nickname;
    private Long storyId;
    private String content;
    private Date createTime;
}
