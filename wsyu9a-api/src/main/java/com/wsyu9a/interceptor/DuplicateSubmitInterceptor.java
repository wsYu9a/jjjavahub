package com.wsyu9a.interceptor;

import com.wsyu9a.annotation.PreventDuplicateSubmit;
import com.wsyu9a.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DuplicateSubmitInterceptor implements HandlerInterceptor {
    
    private final Map<String, Long> submitHistory = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    
    public DuplicateSubmitInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        PreventDuplicateSubmit annotation = handlerMethod.getMethodAnnotation(PreventDuplicateSubmit.class);
        if (annotation == null) {
            return true;
        }

        String username = (String) request.getAttribute("username");
        if (username == null) {
            return true;
        }

        // 生成提交标识
        String submitKey = username + ":" + request.getRequestURI() + ":" + request.getQueryString();
        
        // 检查是否重复提交
        long currentTime = System.currentTimeMillis();
        Long lastSubmitTime = submitHistory.get(submitKey);
        if (lastSubmitTime != null && currentTime - lastSubmitTime < annotation.interval()) {
            log.warn("重复提交: username={}, uri={}, interval={}ms", 
                username, request.getRequestURI(), currentTime - lastSubmitTime);
                
            response.setContentType("application/json;charset=UTF-8");
            Result<?> result = Result.fail(annotation.message());
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }

        // 记录本次提交时间
        submitHistory.put(submitKey, currentTime);
        
        // 清理旧的提交记录
        if (submitHistory.size() > 1000) {
            submitHistory.entrySet().removeIf(entry -> 
                currentTime - entry.getValue() > 60000);
        }

        return true;
    }
} 