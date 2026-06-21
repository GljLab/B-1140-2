package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class StoryItemDTO {
    private Long id;
    private Long storyId;
    private Long pictureId;
    private String pictureUrl;
    private String pictureName;
    private String content;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
}
