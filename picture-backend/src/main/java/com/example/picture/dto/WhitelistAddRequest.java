package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class WhitelistAddRequest {
    private Long pictureId1;
    private Long pictureId2;
    private List<Long> pictureIds;
}
