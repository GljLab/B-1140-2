package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DiscoverPictureDTO {
    private Long id;
    private String name;
    private String url;
    private Date createTime;
    private Long authorId;
    private String authorNickname;
    private long likeCount;
    private long commentCount;
    private long favoriteCount;
    private List<TagDTO> tags;
    private Date favoriteTime;
}
