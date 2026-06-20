package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class AlbumDTO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String coverUrl;
    private Long coverPictureId;
    private Boolean isPublic;
    private Boolean isDefault;
    private Integer displayOrder;
    private Date createTime;
    private Date updateTime;
    private Integer pictureCount;
    private Long totalSize;
    private Date lastUploadTime;
    private List<PictureDTO> pictures;
    private Boolean isCollaboration;
    private String userRole;
    private String creatorNickname;
    private String creatorUsername;
    private Integer collaboratorCount;
}
