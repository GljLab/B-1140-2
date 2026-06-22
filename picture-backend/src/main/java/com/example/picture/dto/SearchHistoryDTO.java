package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SearchHistoryDTO {
    private Long id;
    private String keyword;
    private String searchParams;
    private Date searchTime;
}
