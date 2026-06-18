package com.example.picture.dto;

import lombok.Data;

@Data
public class ShareCreateRequest {
    private String shareType;
    private Long pictureId;
    private Long albumId;
    private String password;
    private Integer expireDays;
}
