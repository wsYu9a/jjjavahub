package com.wsyu9a.service;

import com.wsyu9a.dto.docker.DockerEnvDTO;
import com.wsyu9a.exception.BusinessException;
import java.util.List;

public interface DockerService {
    
    /**
     * 启动题目环境
     * @param problemId 题目ID
     * @return 环境信息
     * @throws BusinessException 业务异常
     */
    DockerEnvDTO startEnvironment(Long problemId) throws BusinessException;

    /**
     * 停止题目环境
     * @param problemId 题目ID
     * @throws BusinessException 业务异常
     */
    void stopEnvironment(Long problemId) throws BusinessException;

    /**
     * 获取环境状态
     * @param problemId 题目ID
     * @return 环境信息
     */
    DockerEnvDTO getEnvironmentStatus(Long problemId);

    /**
     * 获取所有运行中的容器
     * @return 容器列表
     * @throws BusinessException 业务异常
     */
    List<DockerEnvDTO> listAllContainers() throws BusinessException;

    /**
     * 延长容器运行时间
     * @param problemId 题目ID
     * @param minutes 延长时间(分钟)
     * @throws BusinessException 业务异常
     */
    void extendContainerTime(Long problemId, Integer minutes) throws BusinessException;
} 