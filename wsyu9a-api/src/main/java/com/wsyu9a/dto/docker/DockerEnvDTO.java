package com.wsyu9a.dto.docker;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DockerEnvDTO {
    private Long problemId;     // 题目ID
    private String status;      // running, stopped, starting
    private String url;         // 环境访问地址
    private LocalDateTime expireTime;  // 环境过期时间
    private String containerId; // Docker容器ID
    private Integer port;       // 映射的端口
    private String flag;        // 环境变量flag
} 