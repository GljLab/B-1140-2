package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SavedSearchDTO {
    private Long id;
    private String name;
    private String searchParams;
    private Date createTime;
    private Date updateTime;
}
