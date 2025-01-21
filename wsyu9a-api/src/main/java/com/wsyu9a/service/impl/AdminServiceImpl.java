package com.wsyu9a.service.impl;

import com.wsyu9a.dto.UserStatusDTO;
import com.wsyu9a.dto.UserRoleDTO;
import com.wsyu9a.dto.PageDTO;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.entity.User;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.UserMapper;
import com.wsyu9a.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAllUsers();
        // 清除敏感信息
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    @Transactional
    public void updateUserStatus(UserStatusDTO userStatusDTO) {
        // 查找用户
        User user = userMapper.findById(userStatusDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能���改管理员状态
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能修改管理员状态");
        }

        // 更新状态
        user.setEnabled(userStatusDTO.getEnabled());
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            userMapper.updateStatus(user);
            log.info("用户状态更新成功: {}", user.getUsername());
        } catch (Exception e) {
            log.error("用户状态更新失败", e);
            throw new BusinessException("更新失败，请稍后重试");
        }
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    @Transactional
    public void updateUserRole(UserRoleDTO userRoleDTO) {
        // 查找用户
        User user = userMapper.findById(userRoleDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证角色值
        String role = userRoleDTO.getRole().toUpperCase();
        if (!Arrays.asList("ADMIN", "USER").contains(role)) {
            throw new BusinessException("无效的角色值");
        }

        // 更新角色
        user.setRole(role);
        user.setUpdateTime(LocalDateTime.now());
        
        try {
            userMapper.updateRole(user);
            log.info("用户角色更新成功: {}", user.getUsername());
        } catch (Exception e) {
            log.error("用户角色更新失败", e);
            throw new BusinessException("更新失败，请稍后重试");
        }
    }

    @Override
    public PageResult<User> getUsersByPage(PageDTO pageDTO) {
        // 计算偏移量
        int offset = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
        
        // 获取总数
        long total = userMapper.countUsers(pageDTO.getSearchKey());
        
        // 获取当前页数据
        List<User> users = userMapper.findUsersByPage(pageDTO.getSearchKey(), offset, pageDTO.getPageSize());
        users.forEach(user -> user.setPassword(null));
        
        return new PageResult<>(users, total, pageDTO.getPageSize(), pageDTO.getPageNum());
    }
} 