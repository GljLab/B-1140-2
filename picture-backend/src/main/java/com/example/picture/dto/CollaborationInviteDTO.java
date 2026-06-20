package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CollaborationInviteDTO {
    private Long id;
    private Long albumId;
    private String albumName;
    private Long inviterId;
    private String inviterUsername;
    private String inviterNickname;
    private Long inviteeId;
    private String inviteeUsername;
    private String inviteeNickname;
    private String status;
    private Date createTime;
    private Date updateTime;
}
