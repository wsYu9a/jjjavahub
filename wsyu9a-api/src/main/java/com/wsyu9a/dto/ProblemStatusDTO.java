package com.wsyu9a.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProblemStatusDTO {
    @NotNull(message = "题目ID不能为空")
    private Long problemId;
    
    @NotNull(message = "状态不能为空")
    private Boolean enabled;
} 