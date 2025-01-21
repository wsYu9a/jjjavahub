package com.wsyu9a.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementDTO {
    private Long id;
    
    @NotBlank(message = "公告标题不能为空")
    private String title;
    
    @NotBlank(message = "公告内容不能为空")
    private String content;
    
    @NotNull(message = "请选择是否重要")
    private Boolean important;
    
    private Boolean enabled = true;
} 