package com.example.picture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExportRequest {
    private List<Long> pictureIds;
    private Long albumId;
    private String quality;
    private String format;
    private String type;
}
