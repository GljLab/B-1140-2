package com.example.picture.dto;

import lombok.Data;

@Data
public class PrivatePasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}
