package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class DedupResultDTO {
    private Integer totalPicturesScanned;
    private Integer duplicateGroupCount;
    private Integer similarGroupCount;
    private Integer duplicatePictureCount;
    private Long totalReclaimableSize;
    private List<DuplicateGroupDTO> duplicateGroups;
    private List<DuplicateGroupDTO> similarGroups;
}
