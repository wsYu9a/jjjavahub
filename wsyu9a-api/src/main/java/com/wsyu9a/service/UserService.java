package com.wsyu9a.service;

import com.wsyu9a.dto.ResetPasswordDTO;
import com.wsyu9a.dto.UserDTO;
import com.wsyu9a.entity.User;
import com.wsyu9a.dto.LoginDTO;
import com.wsyu9a.dto.LoginResponseDTO;
import com.wsyu9a.dto.UserStatsDTO;

public interface UserService {
    /**
     * 用户注册
     *
     * @param userDTO 用户注册信息
     * @return 注册成功的用户信息（不包含密码）
     */
    User register(UserDTO userDTO);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);

    /**
     * 重置密码
     *
     * @param resetPasswordDTO 重置密码信息
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应信息
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    UserStatsDTO getUserStats(String username);
} 