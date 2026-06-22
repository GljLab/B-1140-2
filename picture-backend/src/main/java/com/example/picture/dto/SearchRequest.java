package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SearchRequest {

    private String keyword;
    private String searchType;
    private List<Long> albumIds;
    private List<Long> tagIds;
    private List<String> colors;
    private Date startDate;
    private Date endDate;
    private Integer minWidth;
    private Integer maxWidth;
    private Integer minHeight;
    private Integer maxHeight;
    private Long minSize;
    private Long maxSize;
    private Boolean hasLocation;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer pageSize;
}
