package com.wsyu9a.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class ProblemDTO {
    private Long id;
    
    @NotBlank(message = "题目标题不能为空")
    private String title;
    
    @NotBlank(message = "题目简述不能为空")
    private String summary;
    
    @NotBlank(message = "题目详解不能为空")
    private String detail;
    
    @NotBlank(message = "flag不能为空")
    private String flag;
    
    @NotNull(message = "积分不能为空")
    @Min(value = 1, message = "积分最小为1")
    @Max(value = 100, message = "积分最大为100")
    private Integer score;
    
    @NotBlank(message = "难度不能为空")
    private String difficulty;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    private Boolean enabled = true;
} 