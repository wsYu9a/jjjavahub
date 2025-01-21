package com.wsyu9a.dto;

import lombok.Data;

@Data
public class UploadResult {
    private String content;  // 文件内容
    private String path;     // 相对路径
} 