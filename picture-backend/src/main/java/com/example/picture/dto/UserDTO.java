package com.example.picture.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private Date createTime;
}
