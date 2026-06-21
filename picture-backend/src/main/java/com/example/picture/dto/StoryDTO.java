package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class StoryDTO {
    private Long id;
    private Long userId;
    private String authorNickname;
    private String authorUsername;
    private String title;
    private String description;
    private String coverUrl;
    private String layout;
    private Integer status;
    private Boolean isPublic;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Date createTime;
    private Date updateTime;
    private Date publishTime;
    private Integer itemCount;
    private List<StoryItemDTO> items;
    private Boolean liked;
    private Boolean isOwner;
}
