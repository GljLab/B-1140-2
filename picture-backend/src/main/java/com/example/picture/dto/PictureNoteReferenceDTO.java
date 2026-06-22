package com.example.picture.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PictureNoteReferenceDTO {
    private Long id;
    private Long noteId;
    private Long pictureId;
    private Integer sortOrder;
    private Date createTime;

    private String pictureUrl;
    private String pictureName;
}
