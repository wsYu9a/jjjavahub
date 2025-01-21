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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/api/admin/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/list")
    public Result<PageResult<Problem>> getProblems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) Long categoryId) {
        try {
            log.info("获取题目列表请求: pageNum={}, pageSize={}, searchKey={}, categoryId={}", 
                    pageNum, pageSize, searchKey, categoryId);
            PageResult<Problem> pageResult = problemService.getProblems(pageNum, pageSize, searchKey, categoryId);
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
            Problem problem = problemService.addProblem(problemDTO);
            return Result.success("添加成功", problem);
        } catch (Exception e) {
            log.error("添加题目失败", e);
            return Result.fail(e.getMessage());
        }
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
    public Result<String> uploadDockerCompose(
            @RequestParam("file") MultipartFile file,
            @RequestParam("problemId") Long problemId) {
        try {
            log.info("开始上传Docker Compose文件, problemId: {}, fileName: {}", 
                    problemId, file.getOriginalFilename());
            String filePath = problemService.uploadDockerCompose(file, problemId);
            return Result.success("上传成功", filePath);
        } catch (BusinessException e) {
            log.error("上传Docker Compose文件失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("上传Docker Compose文件失败", e);
            return Result.fail("上传失败，请稍后重试");
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
            // 调用服务类的方法处理文件上传
            return problemService.uploadReadme(file, assets);
        } catch (Exception e) {
            // 错误处理
            return Result.fail("上传失败，请稍后重试");
        }
    }
} 