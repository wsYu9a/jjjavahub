package com.wsyu9a.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserRoleDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "角色不能为空")
    private String role;
} 