package com.wsyu9a.interceptor;

import com.wsyu9a.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepeatSubmitInterceptor implements HandlerInterceptor {
    
    private final Map<String, Long> cache = new HashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                String key = generateKey(request);
                long now = System.currentTimeMillis();
                synchronized (cache) {
                    Long lastSubmitTime = cache.get(key);
                    if (lastSubmitTime != null && now - lastSubmitTime < annotation.interval()) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"code\":400,\"message\":\"请勿重复提交\"}");
                        return false;
                    }
                    cache.put(key, now);
                }
            }
        }
        return true;
    }
    
    private String generateKey(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return username + ":" + request.getRequestURI();
    }
}