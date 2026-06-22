package com.example.picture.dto;

import lombok.Data;

@Data
public class SearchSuggestionDTO {
    private String text;
    private String type;
    private Long count;
}
