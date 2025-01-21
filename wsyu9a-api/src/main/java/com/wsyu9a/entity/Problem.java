package com.wsyu9a.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Problem {
    private Long id;
    private String title;
    private String summary;
    private String detail;
    private String flag;
    private Integer score;
    private String difficulty;
    private Long categoryId;
    private String dockerComposePath;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String categoryName;
    private String readmePath;
} 