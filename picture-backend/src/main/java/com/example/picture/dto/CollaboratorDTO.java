package com.example.picture.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CollaboratorDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private Date joinedTime;
    private Integer contributedPictures;
}
