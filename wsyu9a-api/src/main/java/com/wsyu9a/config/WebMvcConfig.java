package com.wsyu9a.config;

import com.wsyu9a.interceptor.DuplicateSubmitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final DuplicateSubmitInterceptor duplicateSubmitInterceptor;
    
    public WebMvcConfig(DuplicateSubmitInterceptor duplicateSubmitInterceptor) {
        this.duplicateSubmitInterceptor = duplicateSubmitInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(duplicateSubmitInterceptor)
                .addPathPatterns("/**");
    }
} 