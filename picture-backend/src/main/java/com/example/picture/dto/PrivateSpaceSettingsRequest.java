package com.example.picture.dto;

import lombok.Data;

@Data
public class PrivateSpaceSettingsRequest {
    private String disguiseType;
    private Integer autoLockTime;
    private Boolean guestModeEnabled;
}
