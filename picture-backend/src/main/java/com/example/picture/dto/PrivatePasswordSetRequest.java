package com.example.picture.dto;

import lombok.Data;

@Data
public class PrivatePasswordSetRequest {
    private String password;
    private String passwordType;
}
