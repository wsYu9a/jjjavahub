package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.UserStatusDTO;
import com.wsyu9a.dto.UserRoleDTO;
import com.wsyu9a.dto.PageDTO;
import com.wsyu9a.entity.User;
import com.wsyu9a.service.AdminService;
import com.wsyu9a.util.JwtUtil;
import com.wsyu9a.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    @GetMapping("/getUserInfo")
    public Result<PageResult<User>> getUserInfo(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String searchKey
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.fail(401, "未登录");
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return Result.fail(401, "登录已过期");
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            User user = adminService.findByUsername(username);
            if (user == null || !"ADMIN".equals(user.getRole())) {
                return Result.fail(403, "无权限访问");
            }

            log.info("获取用户列表, 页码: {}, 每页数量: {}, 搜索关键字: {}", pageNum, pageSize, searchKey);
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPageNum(pageNum);
            pageDTO.setPageSize(pageSize);
            pageDTO.setSearchKey(searchKey);
            
            PageResult<User> pageResult = adminService.getUsersByPage(pageDTO);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.fail("获取用户信息失败");
        }
    }

    @PostMapping("/setUserStatus")
    public Result<Void> setUserStatus(@Validated @RequestBody UserStatusDTO userStatusDTO) {
        try {
            log.info("设置用户状态: {}", userStatusDTO);
            adminService.updateUserStatus(userStatusDTO);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("设置用户状态失败", e);
            return Result.fail("设置用户状态失败");
        }
    }

    @PostMapping("/setUserRole")
    public Result<Void> setUserRole(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @Validated @RequestBody UserRoleDTO userRoleDTO
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.fail(401, "未登录");
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return Result.fail(401, "登录已过期");
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            User admin = adminService.findByUsername(username);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                return Result.fail(403, "无权限访问");
            }

            log.info("设置用户角色: {}", userRoleDTO);
            adminService.updateUserRole(userRoleDTO);
            return Result.success("更新成功", null);
        } catch (BusinessException e) {
            log.warn("设置用户角色失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("设置用户角色失败", e);
            return Result.fail("设置用户角色失败");
        }
    }
} 