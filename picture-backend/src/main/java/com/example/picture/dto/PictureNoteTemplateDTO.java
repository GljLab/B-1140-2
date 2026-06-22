package com.example.picture.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PictureNoteTemplateDTO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String content;
    private String category;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
    private Boolean isSystem;
}
