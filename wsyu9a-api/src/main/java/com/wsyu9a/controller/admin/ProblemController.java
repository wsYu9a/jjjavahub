package com.wsyu9a.controller.admin;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.ProblemDTO;
import com.wsyu9a.dto.ProblemStatusDTO;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/admin/problem")
@RequiredArgsConstructor
public class ProblemController {

    @Value("${problem.upload.attachment-path:${user.dir}/upload/fujian}")
    private String attachmentPath;

    @Value("${problem.upload.docker-compose-path:${user.dir}/upload/docker-compose}")
    private String dockerComposePath;

    private final ProblemService problemService;

    @GetMapping("/list")
    public Result<PageResult<Problem>> getProblems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty) {
        try {
            log.info("获取题目列表请求: pageNum={}, pageSize={}, searchKey={}, categoryId={}, difficulty={}", 
                    pageNum, pageSize, searchKey, categoryId, difficulty);
            PageResult<Problem> pageResult = problemService.getProblems(pageNum, pageSize, searchKey, categoryId, difficulty);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取题目列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Problem> getProblemDetail(@PathVariable Long id) {
        try {
            Problem problem = problemService.getProblemById(id);
            return Result.success("获取成功", problem);
        } catch (Exception e) {
            log.error("获取题目详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Problem> addProblem(@Validated @RequestBody ProblemDTO problemDTO) {
        try {
            // 验证文件路径
            if (!problemDTO.getDetail().startsWith("readme/") || !problemDTO.getDetail().endsWith("/README.md")) {
                return Result.fail("无效的题目说明文件路径");
            }
            if (!problemDTO.getDockerComposePath().startsWith("docker-compose/") || 
                !problemDTO.getDockerComposePath().endsWith("/docker-compose.yml")) {
                return Result.fail("无效的Docker配置文件路径");
            }
            if (problemDTO.getAttachmentPath() != null && 
                (!problemDTO.getAttachmentPath().startsWith("fujian/") || 
                 !isValidAttachmentPath(problemDTO.getAttachmentPath()))) {
                return Result.fail("无效的附件路径");
            }

            Problem problem = problemService.addProblem(problemDTO);
            return Result.success("添加成功", problem);
        } catch (BusinessException e) {
            log.error("添加题目失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("添加题目失败", e);
            return Result.fail("系统错误，添加失败");
        }
    }

    private boolean isValidAttachmentPath(String path) {
        // 验证附件路径格式
        String[] parts = path.split("/");
        return parts.length == 3 && 
               parts[0].equals("fujian") && 
               parts[1].matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @PutMapping("/update")
    public Result<Problem> updateProblem(@Validated @RequestBody ProblemDTO problemDTO) {
        try {
            Problem problem = problemService.updateProblem(problemDTO);
            return Result.success("更新成功", problem);
        } catch (Exception e) {
            log.error("更新题目失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProblem(@PathVariable Long id) {
        try {
            problemService.deleteProblem(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除题目失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/upload/docker-compose")
    public Result<String> uploadDockerCompose(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.fail("请选择要上传的文件");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".yml") && !originalFilename.endsWith(".yaml"))) {
                return Result.fail("只能上传yml或yaml格式的文件");
            }

            // 创建唯一目录
            String dirId = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(dockerComposePath, dirId);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve("docker-compose.yml");
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回相对路径，加上docker-compose文件夹
            return Result.success("上传成功", "docker-compose/" + dirId + "/docker-compose.yml");

        } catch (Exception e) {
            log.error("上传Docker Compose文件失败", e);
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

    @PutMapping("/status")
    public Result<Void> updateProblemStatus(@Validated @RequestBody ProblemStatusDTO statusDTO) {
        try {
            problemService.updateProblemStatus(statusDTO);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新题目状态失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/upload/readme")
    public Result<String> uploadReadme(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "assets", required = false) MultipartFile[] assets) {
        try {
            Result<String> result = problemService.uploadReadme(file, assets);
            if (result.getCode() == 200 && result.getData() != null) {
                // 在返回的路径前加上readme文件夹
                return Result.success(result.getMessage(), "readme/" + result.getData());
            }
            return result;
        } catch (Exception e) {
            return Result.fail("上传失败，请稍后重试");
        }
    }

    @PostMapping("/upload/attachments")
    public Result<String> uploadAttachments(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.fail("请选择要上传的文件");
            }

            // 创建文件夹
            String dirId = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(attachmentPath, dirId);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            List<String> fileNames = new ArrayList<>();

            // 保存文件
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    continue;
                }

                // 检查文件类型
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                if (!isAllowedExtension(extension)) {
                    return Result.fail("不支持的文件类型: " + extension);
                }

                // 检查文件大小
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                    return Result.fail("文件大小不能超过10MB");
                }

                Path filePath = uploadDir.resolve(originalFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                fileNames.add(originalFilename);
            }

            // 返回相对路径，只返回第一个文件的路径
            String fileName = fileNames.isEmpty() ? "" : fileNames.get(0);
            return Result.success("上传成功", "fujian/" + dirId + "/" + fileName);
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

    private boolean isAllowedExtension(String extension) {
        return ".txt".equals(extension) ||
                ".zip".equals(extension) ||
                ".jar".equals(extension);
    }
} 