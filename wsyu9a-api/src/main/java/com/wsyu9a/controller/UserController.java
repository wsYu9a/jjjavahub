package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.dto.UserDTO;
import com.wsyu9a.dto.ResetPasswordDTO;
import com.wsyu9a.dto.LoginDTO;
import com.wsyu9a.dto.LoginResponseDTO;
import com.wsyu9a.dto.UserStatsDTO;
import com.wsyu9a.entity.User;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/regist")
    public Result<User> register(@Validated @RequestBody UserDTO userDTO) {
        log.info("注册请求参数: {}", userDTO);
        User user = userService.register(userDTO);
        log.info("注册成功: {}", user);
        return Result.success("注册成功", user);
    }

    @PostMapping("/resetpwd")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.info("重置密码请求参数: {}", resetPasswordDTO);
        userService.resetPassword(resetPasswordDTO);
        return Result.success("密码重置成功",null);
    }

    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = userService.login(loginDTO);
            log.info("登录成功，用户名: {}, 角色: {}", loginDTO.getUsername(), response.getRole());
            return Result.success("登录成功", response);
        } catch (BusinessException e) {
            log.warn("登录失败: {}, 用户名: {}", e.getMessage(), loginDTO.getUsername());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("登录异常, 用户名: " + loginDTO.getUsername(), e);
            return Result.fail("系统异常，请联系管理员");
        }
    }

    @GetMapping("/stats")
    public Result<UserStatsDTO> getUserStats(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            UserStatsDTO stats = userService.getUserStats(username);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            return Result.fail("获取用户统计信息失败");
        }
    }
} 