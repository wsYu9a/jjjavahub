package com.wsyu9a.service.impl;

import com.wsyu9a.dto.UserDTO;
import com.wsyu9a.entity.User;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.UserMapper;
import com.wsyu9a.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Base64;
import java.security.MessageDigest;
import com.wsyu9a.dto.ResetPasswordDTO;
import com.wsyu9a.dto.LoginDTO;
import com.wsyu9a.util.JwtUtil;
import com.wsyu9a.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public User register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (findByUsername(userDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (findByEmail(userDTO.getEmail()) != null) {
            throw new BusinessException("邮箱已存在");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodePassword(userDTO.getPassword()));
        user.setRole("USER");
        user.setEnabled(true);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        try {
            userMapper.insert(user);
            log.info("用户注册成功: {}", user.getUsername());
        } catch (Exception e) {
            log.error("用户注册失败", e);
            throw new BusinessException("注册失败，请稍后重试");
        }

        // 返回用户信息（清除密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 1. 根据邮箱查找用户
        User user = userMapper.findByEmail(resetPasswordDTO.getEmail());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 验证旧密码
        String encodedOldPassword = encodePassword(resetPasswordDTO.getOldPassword());
        if (!encodedOldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 3. 验证新密码不能与旧密码相同
        if (resetPasswordDTO.getOldPassword().equals(resetPasswordDTO.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        
        // 4. 更新密码
        user.setPassword(encodePassword(resetPasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        
        // 5. 保存到数据库
        int rows = userMapper.updateById(user);
        if (rows != 1) {
            throw new BusinessException("密码重置失败");
        }
    }

    private String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        try {
            log.info("开始处理登录请求，用户名: {}", loginDTO.getUsername());
            
            // 参数校验
            if (loginDTO == null) {
                throw new BusinessException("登录参数不能为空");
            }
            
            // 查找用户
            User user = findByUsername(loginDTO.getUsername());
            if (user == null) {
                log.warn("用户名不存在: {}", loginDTO.getUsername());
                throw new BusinessException("用户名或密码错误");
            }

            // 验证密码
            String encodedPassword = encodePassword(loginDTO.getPassword());
            if (!encodedPassword.equals(user.getPassword())) {
                log.warn("密码错误，用户名: {}", loginDTO.getUsername());
                throw new BusinessException("用户名或密码错误");
            }

            // 检查用户状态
            if (!user.getEnabled()) {
                log.warn("账号已禁用: {}", loginDTO.getUsername());
                throw new BusinessException("账号已被禁用");
            }

            // 生成token
            String token = jwtUtil.generateToken(user.getUsername());
            log.info("登录成功，用���名: {}, 角色: {}", loginDTO.getUsername(), user.getRole());
            
            return LoginResponseDTO.builder()
                    .token(token)
                    .role(user.getRole().toUpperCase())
                    .username(user.getUsername())
                    .build();
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录过程发生异常，用户名: " + loginDTO.getUsername(), e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }
}