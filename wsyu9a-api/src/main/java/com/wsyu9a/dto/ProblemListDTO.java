package com.wsyu9a.dto;

import lombok.Data;
import com.wsyu9a.entity.Difficulty;
import java.time.LocalDateTime;

@Data
public class ProblemListDTO {
    private Long id;
    private String title;
    private Long categoryId;
    private String categoryName;  // 只在需要展示分类名称的地方使用
    private Difficulty difficulty;
    private Integer score;
    private String summary;
    private Boolean enabled;
    private LocalDateTime createTime;
} 