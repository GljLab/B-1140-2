package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PictureDTO {
    private Long id;
    private String name;
    private String url;
    private String originalUrl;
    private Boolean hasWatermark;
    private Long size;
    private Date createTime;
    private Date updateTime;
    private List<AlbumSimpleDTO> albums;
    private List<TagDTO> tags;
    private Boolean deleted;
    private Date deleteTime;
    private Integer remainingDays;
    private Boolean isPublic;
    private Boolean isPrivate;
    private Long userId;
    private String authorNickname;
    private long likeCount;
    private long commentCount;
    private long favoriteCount;
    private Boolean isLiked;
    private Boolean isFavorited;
    private Integer width;
    private Integer height;
    private Date shootTime;
    private String dominantColor;
    private Double latitude;
    private Double longitude;
    private Boolean hasLocation;
    private Double similarity;
}
