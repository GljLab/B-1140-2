package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
    private String relatedType;
    private Boolean isRead;
    private Date createTime;
    private Long inviteId;
    private String inviteStatus;
    private String fromNickname;
}
