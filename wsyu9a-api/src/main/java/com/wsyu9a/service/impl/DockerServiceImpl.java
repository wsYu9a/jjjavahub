package com.wsyu9a.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.wsyu9a.dto.docker.DockerEnvDTO;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.ProblemMapper;
import com.wsyu9a.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DockerServiceImpl implements DockerService {

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ProblemMapper problemMapper;

    @Value("${docker.host}")
    private String dockerHost;

    @Value("${docker.env.expire-minutes}")
    private Integer expireMinutes;

    @Value("${problem.docker-compose.upload-path}")
    private String uploadPath;

    @Override
    public DockerEnvDTO startEnvironment(Long problemId) throws BusinessException {
        Problem problem = problemMapper.findById(problemId);
        if (problem == null) {
            throw new BusinessException("题目不存在");
        }

        String composePath = problem.getDockerComposePath();
        if (composePath == null) {
            throw new BusinessException("题目环境未配置");
        }

        try {
            // 生成随机端口
            int port = generateRandomPort();
            
            // 读取docker-compose.yml文件
            String newPath = composePath.replaceFirst("docker-compose/", "");
            File composeFile = new File(uploadPath, newPath);
            if (!composeFile.exists()) {
                throw new BusinessException("docker-compose.yml文件不存在");
            }

            // 解析docker-compose.yml
            Yaml yaml = new Yaml();
            Map<String, Object> composeConfig;
            try (FileInputStream fis = new FileInputStream(composeFile)) {
                composeConfig = yaml.load(fis);
            }

            // 添加调试日志
            log.debug("Docker compose config: {}", composeConfig);

            String image = (String) composeConfig.get("image");
            if (image == null) {
                throw new BusinessException("docker-compose.yml配置错误:未找到image配置");
            }
            
            // 确保image不为空且格式正确
            if (!image.contains("/")) {
                throw new BusinessException("docker-compose.yml配置错误:image格式不正确，应为 repository/image 格式");
            }

            Integer containerPort = (Integer) composeConfig.get("port");
            if (containerPort == null) {
                containerPort = 80;  // 默认使用80端口
                log.warn("未找到port配置，使用默认端口80");
            }

            // 准备环境变量
            List<String> envList = new ArrayList<>();
            envList.add("flag=" + problem.getFlag());  // 从题目配置中获取flag

            // 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withExposedPorts(ExposedPort.tcp(containerPort))
                .withPortBindings(PortBinding.parse(port + ":" + containerPort))
                .withName("problem-" + problemId + "-web-" + System.currentTimeMillis())
                .withEnv(envList)  // 设置环境变量
                .exec();

            // 启动容器
            dockerClient.startContainerCmd(container.getId()).exec();

            // 设置过期时间
            LocalDateTime expireTime = LocalDateTime.now().plusMinutes(expireMinutes);

            // 返回环境信息
            return DockerEnvDTO.builder()
                .status("running")
                .url("http://" + dockerHost + ":" + port)
                .expireTime(expireTime)
                .containerId(container.getId())
                .port(port)
                .build();

        } catch (Exception e) {
            log.error("启动环境失败", e);
            throw new BusinessException("启动环境失败: " + e.getMessage());
        }
    }

    @Override
    public void stopEnvironment(Long problemId) throws BusinessException {
        try {
            List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Collections.singletonList("problem-" + problemId))
                .exec();

            for (Container container : containers) {
                dockerClient.stopContainerCmd(container.getId()).exec();
                dockerClient.removeContainerCmd(container.getId()).exec();
            }
        } catch (Exception e) {
            log.error("停止环境失败", e);
            throw new BusinessException("停止环境失败: " + e.getMessage());
        }
    }

    @Override
    public DockerEnvDTO getEnvironmentStatus(Long problemId) {
        try {
            List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Collections.singletonList("problem-" + problemId))
                .exec();
            
            if (containers.isEmpty()) {
                return null;
            }
            
            Container container = containers.get(0);
            String containerId = container.getId();

            // 使用 inspectContainer 获取详细信息
            var inspectResponse = dockerClient.inspectContainerCmd(containerId).exec();
            
            // 获取环境变量和过期时间
            String[] envVars = inspectResponse.getConfig().getEnv();
            LocalDateTime expireTime = null;
            if (envVars != null) {
                for (String env : envVars) {
                    if (env.startsWith("expire_time=")) {
                        expireTime = LocalDateTime.parse(env.substring(11));
                        break;
                    }
                }
            }

            return DockerEnvDTO.builder()
                .problemId(problemId)
                .containerId(containerId)
                .status("running")
                .url("http://" + dockerHost + ":" + container.getPorts()[0].getPublicPort())
                .port(container.getPorts()[0].getPublicPort())
                .expireTime(expireTime)
                .build();
        } catch (Exception e) {
            log.error("Failed to get environment status", e);
            return null;
        }
    }

    @Override
    public void extendContainerTime(Long problemId, Integer minutes) throws BusinessException {
        try {
            // 获取容器列表
            List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Collections.singletonList("problem-" + problemId))
                .exec();

            if (containers.isEmpty()) {
                throw new BusinessException("未找到运行中的容器");
            }

            // 获取第一个匹配的容器
            Container container = containers.get(0);
            String containerId = container.getId();

            // 获取容器详细信息
            var inspectResponse = dockerClient.inspectContainerCmd(containerId).exec();
            String[] envVars = inspectResponse.getConfig().getEnv();
            String flag = null;

            // 获取原有的 flag
            if (envVars != null) {
                for (String env : envVars) {
                    if (env.startsWith("flag=")) {
                        flag = env.substring(5);
                        break;
                    }
                }
            }

            // 停止并删除旧容器
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();

            // 重新创建并启动容器，设置新的过期时间
            Problem problem = problemMapper.findById(problemId);
            if (problem == null) {
                throw new BusinessException("题目不存在");
            }

            // 读取原始配置
            String composePath = problem.getDockerComposePath();
            String newPath = composePath.replaceFirst("docker-compose/", "");
            File composeFile = new File(uploadPath, newPath);
            
            Yaml yaml = new Yaml();
            Map<String, Object> composeConfig;
            try (FileInputStream fis = new FileInputStream(composeFile)) {
                composeConfig = yaml.load(fis);
            }

            String image = (String) composeConfig.get("image");
            Integer containerPort = (Integer) composeConfig.get("port");
            if (containerPort == null) {
                containerPort = 80;
            }

            // 生成新的随机端口
            int port = generateRandomPort();

            // 准备环境变量，包括新的过期时间
            List<String> envList = new ArrayList<>();
            envList.add("flag=" + (flag != null ? flag : problem.getFlag()));
            LocalDateTime newExpireTime = LocalDateTime.now().plusMinutes(minutes);
            envList.add("expire_time=" + newExpireTime.toString());

            // 创建新容器
            CreateContainerResponse newContainer = dockerClient.createContainerCmd(image)
                .withExposedPorts(ExposedPort.tcp(containerPort))
                .withPortBindings(PortBinding.parse(port + ":" + containerPort))
                .withName("problem-" + problemId + "-web-" + System.currentTimeMillis())
                .withEnv(envList)
                .exec();

            // 启动新容器
            dockerClient.startContainerCmd(newContainer.getId()).exec();

            log.info("Container recreated with new expiration time: {}", newExpireTime);
        } catch (Exception e) {
            log.error("延长容器运行时间失败", e);
            throw new BusinessException("延长容器运行时间失败: " + e.getMessage());
        }
    }

    @Override
    public List<DockerEnvDTO> listAllContainers() throws BusinessException {
        try {
            // 获取所有运行中的容器
            List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Collections.singletonList("problem-"))
                .exec();

            // 转换为 DTO 列表
            return containers.stream()
                .map(container -> {
                    String containerId = container.getId();
                    String name = container.getNames()[0].substring(1); // 移除开头的 '/'
                    
                    // 从容器名称中提取题目ID
                    String[] parts = name.split("-");
                    Long problemId = Long.parseLong(parts[1]);
                    
                    // 获取端口映射
                    int port = container.getPorts()[0].getPublicPort();
                    
                    // 获取容器详细信息
                    String flag = "";
                    LocalDateTime expireTime = null;
                    try {
                        var inspectResponse = dockerClient.inspectContainerCmd(containerId).exec();
                        String[] envVars = inspectResponse.getConfig().getEnv();
                        if (envVars != null) {
                            for (String env : envVars) {
                                if (env.startsWith("flag=")) {
                                    flag = env.substring(5);
                                } else if (env.startsWith("expire_time=")) {
                                    expireTime = LocalDateTime.parse(env.substring(11));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to inspect container {}: {}", containerId, e.getMessage());
                    }

                    return DockerEnvDTO.builder()
                        .problemId(problemId)
                        .containerId(containerId)
                        .status("running")
                        .url("http://" + dockerHost + ":" + port)
                        .port(port)
                        .flag(flag)
                        .expireTime(expireTime)
                        .build();
                })
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取容器列表失败", e);
            throw new BusinessException("获取容器列表失败: " + e.getMessage());
        }
    }

    private int generateRandomPort() {
        // 生成随机端口（10000-65535之间）
        return (int) (Math.random() * 55535) + 10000;
    }
} 