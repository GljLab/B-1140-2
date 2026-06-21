package com.example.picture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestoreResultDTO {
    private Boolean success;
    private Integer restoredPictures;
    private Integer restoredAlbums;
    private Integer restoredTags;
    private Integer skippedPictures;
    private String message;
}
