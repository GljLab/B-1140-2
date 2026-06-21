package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class DuplicateGroupDTO {
    private String groupId;
    private String groupType;
    private String similarityLevel;
    private Double averageSimilarity;
    private Long totalSize;
    private Long reclaimableSize;
    private Integer pictureCount;
    private List<PictureDedupDTO> pictures;
    private Boolean selected;
    private String recommendedReason;
}
