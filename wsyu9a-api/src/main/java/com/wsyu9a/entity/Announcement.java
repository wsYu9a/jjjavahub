package com.wsyu9a.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long id;
    private String title;           // 公告标题
    private String content;         // 公告内容
    private Boolean important;      // 是否重要
    private Boolean enabled;        // 是否启用
    private Long publisherId;       // 发布者ID
    private String publisherName;   // 发布者名称
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 