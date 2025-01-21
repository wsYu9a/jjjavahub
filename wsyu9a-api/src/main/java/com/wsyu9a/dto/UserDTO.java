package com.wsyu9a.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "用户名只能包含字母、数字、下划线和横线，长度在3-20之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
} 