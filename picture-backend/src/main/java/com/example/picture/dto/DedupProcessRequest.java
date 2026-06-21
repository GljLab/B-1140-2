package com.example.picture.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DedupProcessRequest {
    private List<String> groupIds;
    private Map<String, List<Long>> keepPictureIds;
    private String similarityLevel;
}
