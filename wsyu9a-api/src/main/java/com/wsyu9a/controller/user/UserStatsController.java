package com.wsyu9a.controller.user;

import com.wsyu9a.dto.UserStatsDTO;
import com.wsyu9a.service.UserService;
import com.wsyu9a.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/baseStats")
@RequiredArgsConstructor
public class UserStatsController {
    
    private final UserService userService;

    @GetMapping
    public Result<UserStatsDTO> getUserStats(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        UserStatsDTO stats = userService.getUserStats(username);
        return Result.success(stats);
    }
} 