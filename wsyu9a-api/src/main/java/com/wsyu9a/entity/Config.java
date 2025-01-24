package com.wsyu9a.entity;

import lombok.Data;

@Data
public class Config {
    private Long id;
    private String dockerApi;        // Docker API地址
    private String dockerRegistry;   // Docker镜像仓库地址
    private Integer dockerMaxPort;   // Docker容器最大端口
    private Integer dockerMinPort;   // Docker容器最小端口
    private Integer dockerTime;      // Docker容器超时时间(秒)
    private String createTime;
    private String updateTime;
} 