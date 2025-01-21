package com.wsyu9a.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsyu9a.common.Result;
import com.wsyu9a.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    // 不需要验证的路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/user/login",
            "/api/user/regist",
            "/api/user/resetpwd"
    );
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        // 检查是否是排除的路径
        String path = request.getServletPath();
        if (EXCLUDE_PATHS.stream().anyMatch(p -> pathMatcher.match(p, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            handleAuthError(response, "未登录");
            return;
        }

        token = token.substring(7);

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            handleAuthError(response, "token已过期或无效");
            return;
        }

        // 设置用户信息到请求属性中
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        filterChain.doFilter(request, response);
    }

    private void handleAuthError(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, message)));
    }
} 