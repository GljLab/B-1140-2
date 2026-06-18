package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ShareDTO {
    private Long id;
    private String shareType;
    private Long pictureId;
    private Long albumId;
    private String title;
    private String coverUrl;
    private String shareToken;
    private Boolean hasPassword;
    private Integer expireDays;
    private Date expireTime;
    private Integer viewCount;
    private Date createTime;
    private String shareUrl;
    private UserDTO creator;
    private PictureDTO picture;
    private AlbumDTO album;
    private Boolean expired;
}
