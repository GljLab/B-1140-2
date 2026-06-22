package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PrivateSpaceConfigDTO {
    private Long userId;
    private Boolean hasPassword;
    private String passwordType;
    private String disguiseType;
    private Integer autoLockTime;
    private Boolean guestModeEnabled;
    private Boolean hasSecurityQuestion;
    private String securityQuestion;
    private Boolean isLocked;
    private Integer failedAttempts;
    private Date lastActivityTime;
}
