package com.wsyu9a.controller.user;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.net.URLDecoder;

@Slf4j
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Component("publicProblemController")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public Result<PageResult<Problem>> getPublicProblems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty) {
        try {
            PageResult<Problem> pageResult = problemService.getPublicProblems(
                pageNum, pageSize, searchKey, categoryId, difficulty);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取题目列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Problem> getPublicProblemDetail(@PathVariable Long id) {
        try {
            Problem problem = problemService.getPublicProblemById(id);
            return Result.success("获取成功", problem);
        } catch (Exception e) {
            log.error("获取题目详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/readme/{encodedPath}")
    public Result<String> getReadmeContent(@PathVariable String encodedPath) {
        try {
            // 解码路径
            String path = new String(Base64.getDecoder().decode(encodedPath));
            // 从文件系统读取 README 内容
            String content = problemService.getReadmeContent(path);
            return Result.success("获取成功", content);
        } catch (Exception e) {
            log.error("获取README内容失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/fujian/{encodedPath}")
    public Result<String> getFujianContent(@PathVariable String encodedPath) {
        try {
            // 解码路径
            String path1 = new String(Base64.getDecoder().decode(encodedPath));
            String path = URLDecoder.decode(path1, "UTF-8");
            // 从文件系统读取 README 内容
            String content = problemService.getFujianContent(path);
            return Result.success("获取成功", content);
        } catch (Exception e) {
            log.error("获取README内容失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 