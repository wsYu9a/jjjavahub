package com.wsyu9a.config;

import com.wsyu9a.interceptor.RepeatSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final RepeatSubmitInterceptor repeatSubmitInterceptor;
    
    public WebMvcConfig(RepeatSubmitInterceptor repeatSubmitInterceptor) {
        this.repeatSubmitInterceptor = repeatSubmitInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/**");
    }
} 