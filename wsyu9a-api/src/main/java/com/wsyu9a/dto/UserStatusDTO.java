package com.wsyu9a.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UserStatusDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "状态不能为空")
    private Boolean enabled;
} 