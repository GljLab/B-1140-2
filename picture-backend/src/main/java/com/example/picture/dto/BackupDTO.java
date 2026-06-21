package com.example.picture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BackupDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private Integer pictureCount;
    private Integer albumCount;
    private Integer tagCount;
    private String status;
    private String errorMessage;
    private Date createTime;
    private Date completeTime;
}
