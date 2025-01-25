package com.wsyu9a.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String password;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
    private Boolean enabled;
    private Integer score;
} 