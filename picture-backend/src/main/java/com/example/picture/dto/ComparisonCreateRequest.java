package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class ComparisonCreateRequest {
    private String title;
    private String mode;
    private List<Long> pictureIds;
    private String viewConfig;
    private String thumbnailUrl;
    private Boolean isPublic;
}
