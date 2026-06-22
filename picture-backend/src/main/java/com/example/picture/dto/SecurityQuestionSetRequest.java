package com.example.picture.dto;

import lombok.Data;

@Data
public class SecurityQuestionSetRequest {
    private String question;
    private String answer;
}
