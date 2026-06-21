package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AnnotationReviewDTO {
    private Long id;
    private Long pictureId;
    private String pictureName;
    private String pictureUrl;
    private Long requesterId;
    private String requesterNickname;
    private Long reviewerId;
    private String reviewerNickname;
    private String status;
    private String comment;
    private Date requestTime;
    private Date reviewTime;
    private Date createTime;
    private Date updateTime;
}
