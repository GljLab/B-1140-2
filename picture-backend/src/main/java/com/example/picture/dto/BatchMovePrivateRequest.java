package com.example.picture.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchMovePrivateRequest {
    private List<Long> pictureIds;
}
