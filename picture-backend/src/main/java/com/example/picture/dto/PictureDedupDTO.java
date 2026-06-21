package com.example.picture.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PictureDedupDTO {
    private Long id;
    private String name;
    private String url;
    private Long size;
    private Integer width;
    private Integer height;
    private Date createTime;
    private List<AlbumSimpleDTO> albums;
    private Double similarity;
    private Integer pixels;
    private String qualityScore;
    private Boolean recommended;
    private Boolean selectedForDelete;
}
