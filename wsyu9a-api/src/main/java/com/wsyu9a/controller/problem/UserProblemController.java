package com.wsyu9a.controller.problem;

import com.wsyu9a.common.Result;
import com.wsyu9a.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import com.wsyu9a.exception.BusinessException;
import java.util.concurrent.ConcurrentHashMap;
import com.wsyu9a.annotation.PreventDuplicateSubmit;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.vo.ProblemVO;

@Slf4j
@RestController
@RequestMapping("/api/problems/user")
@RequiredArgsConstructor
public class UserProblemController {

    private final ProblemService problemService;
    private final Map<String, Long> submitHistory = new ConcurrentHashMap<>();
    private static final long SUBMIT_INTERVAL = 1000; // 1秒内不允许重复提交

    /**
     * 提交flag
     */
    @PostMapping("/{id}/submit")
    @PreventDuplicateSubmit(interval = 1000, message = "提交太频繁，请稍后再试")
    public Result<Void> submitFlag(
            @PathVariable Long id,
            @RequestBody Map<String, String> submitData,
            HttpServletRequest request) {
        // 验证输入
        String username = (String) request.getAttribute("username");
        String flag = submitData.get("flag");
        
        if (flag == null || flag.trim().isEmpty()) {
            return Result.fail("Flag不能为空");
        }

        // 生成提交标识
        String submitKey = username + ":" + id + ":" + flag;
        
        // 检查是否重复提交
        long currentTime = System.currentTimeMillis();
        Long lastSubmitTime = submitHistory.get(submitKey);
        if (lastSubmitTime != null && currentTime - lastSubmitTime < SUBMIT_INTERVAL) {
            log.warn("重复提交: username={}, problemId={}, interval={}ms", 
                username, id, currentTime - lastSubmitTime);
            return Result.fail("提交太频繁，请稍后再试");
        }

        try {
            // 记录本次提交时间
            submitHistory.put(submitKey, currentTime);
            
            // 清理旧的提交记录
            if (submitHistory.size() > 1000) {  // 防止内存泄漏
                submitHistory.entrySet().removeIf(entry -> 
                    currentTime - entry.getValue() > 60000);  // 清理1分钟前的记录
            }

            boolean isCorrect = problemService.submitFlag(username, id, flag.trim());
            return isCorrect ? Result.success("提交正确") : Result.fail("Flag不正确");
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("提交flag失败", e);
            return Result.fail("提交失败，请稍后重试");
        }
    }

    @GetMapping
    public Result<PageResult<ProblemVO>> getUserProblems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty,
            HttpServletRequest request) {
        
        String username = (String) request.getAttribute("username");
        PageResult<ProblemVO> result = problemService.getUserProblems(
            username, pageNum, pageSize, searchKey, categoryId, difficulty);
        return Result.success(result);
    }
} 