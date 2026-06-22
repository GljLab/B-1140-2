package com.example.picture.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SlideshowDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String coverUrl;
    private String transition;
    private Integer interval;
    private Boolean loop;
    private Boolean autoStart;
    private String bgMusicUrl;
    private String bgMusicType;
    private Double bgMusicVolume;
    private Boolean bgMusicLoop;
    private String effect;
    private Double effectIntensity;
    private String titlePageText;
    private String textAnimation;
    private String textStyle;
    private String textPosition;
    private Integer status;
    private Boolean isPublic;
    private String shareToken;
    private Boolean allowDownload;
    private Long viewCount;
    private Date createTime;
    private Date updateTime;
    private Date publishTime;
    private Integer itemCount;
    private String authorNickname;
    private Boolean isOwner;
    private String shareUrl;
    private List<SlideshowItemDTO> items;
}
