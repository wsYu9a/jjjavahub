package com.wsyu9a.controller.problem;

import com.wsyu9a.common.Result;
import com.wsyu9a.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class UserProblemController {

    private final ProblemService problemService;

    /**
     * 提交flag
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submitFlag(
            @PathVariable Long id,
            @RequestBody Map<String, String> submitData,
            HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            String flag = submitData.get("flag");
            
            if (flag == null || flag.trim().isEmpty()) {
                return Result.fail("Flag不能为空");
            }

            problemService.submitFlag(username, id, flag.trim());
            return Result.success("提交成功");
        } catch (Exception e) {
            log.error("提交flag失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 