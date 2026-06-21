package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class WhitelistDTO {
    private Long id;
    private Long pictureId1;
    private Long pictureId2;
    private PictureDedupDTO picture1;
    private PictureDedupDTO picture2;
    private Date createTime;
}
