package com.wsyu9a.service;

import com.wsyu9a.dto.UserStatusDTO;
import com.wsyu9a.dto.UserRoleDTO;
import com.wsyu9a.dto.PageDTO;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.entity.User;
import java.util.List;

public interface AdminService {
    /**
     * 获取所有用户信息
     *
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 更新用户状态
     *
     * @param userStatusDTO 用户状态信息
     */
    void updateUserStatus(UserStatusDTO userStatusDTO);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 更新用户角色
     *
     * @param userRoleDTO 用户角色信息
     */
    void updateUserRole(UserRoleDTO userRoleDTO);

    /**
     * 分页获取用户列表
     *
     * @param pageDTO 分页和搜索参数
     * @return 分页结果
     */
    PageResult<User> getUsersByPage(PageDTO pageDTO);
} 