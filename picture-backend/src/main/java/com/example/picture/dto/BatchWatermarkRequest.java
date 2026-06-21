package com.example.picture.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchWatermarkRequest {
    private List<Long> pictureIds;
    private Long templateId;
}
