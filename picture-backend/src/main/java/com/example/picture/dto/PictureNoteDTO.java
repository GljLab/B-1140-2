package com.example.picture.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PictureNoteDTO {
    private Long id;
    private Long pictureId;
    private Long userId;
    private String title;
    private String content;
    private Integer wordCount;
    private Boolean isDraft;
    private Date createTime;
    private Date updateTime;

    private String pictureUrl;
    private String pictureName;
    private List<PictureNoteReferenceDTO> references;
}
