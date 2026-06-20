package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String content;
    private Long relatedId;
    private String relatedType;
    private Boolean isRead;
    private Date createTime;
}
