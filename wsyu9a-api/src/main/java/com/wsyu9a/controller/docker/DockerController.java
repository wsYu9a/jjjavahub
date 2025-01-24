package com.wsyu9a.controller.docker;

import com.wsyu9a.common.Result;
import com.wsyu9a.dto.docker.DockerEnvDTO;
import com.wsyu9a.service.DockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/problems/{problemId}/env")
@RequiredArgsConstructor
public class DockerController {

    private final DockerService dockerService;

    @PostMapping("/start")
    public Result<DockerEnvDTO> startEnvironment(@PathVariable Long problemId) {
        try {
            // 先检查是否已有运行中的容器
            DockerEnvDTO existingEnv = dockerService.getEnvironmentStatus(problemId);
            if (existingEnv != null && "running".equals(existingEnv.getStatus())) {
                return Result.success(existingEnv);  // 如果已经运行，直接返回
            }
            
            // 没有运行中的容器，则启动新环境
            DockerEnvDTO env = dockerService.startEnvironment(problemId);
            return Result.success(env);
        } catch (Exception e) {
            log.error("Failed to start environment", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/stop")
    public Result<Void> stopEnvironment(@PathVariable Long problemId) {
        try {
            log.info("Stopping environment for problem: {}", problemId);
            dockerService.stopEnvironment(problemId);
            return Result.success("环境已停止");
        } catch (Exception e) {
            log.error("Failed to stop environment for problem: " + problemId, e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/status")
    public Result<DockerEnvDTO> getEnvironmentStatus(@PathVariable Long problemId) {
        try {
            log.info("Getting environment status for problem: {}", problemId);
            DockerEnvDTO status = dockerService.getEnvironmentStatus(problemId);
            return Result.success(status);
        } catch (Exception e) {
            log.error("Failed to get environment status for problem: " + problemId, e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/admin/containers")
    public Result<List<DockerEnvDTO>> listAllContainers() {
        try {
            log.info("Getting all containers");
            List<DockerEnvDTO> containers = dockerService.listAllContainers();
            return Result.success(containers);
        } catch (Exception e) {
            log.error("Failed to get containers", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/admin/extend")
    public Result<Void> extendContainerTime(@PathVariable Long problemId, @RequestParam Integer minutes) {
        try {
            log.info("Extending container time for problem: {} by {} minutes", problemId, minutes);
            dockerService.extendContainerTime(problemId, minutes);
            return Result.success("延长运行时间成功");
        } catch (Exception e) {
            log.error("Failed to extend container time", e);
            return Result.fail(e.getMessage());
        }
    }
} 