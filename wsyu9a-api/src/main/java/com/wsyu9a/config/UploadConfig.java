package com.wsyu9a.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfig {
    
    @Value("${problem.upload.readme-path}")
    private String readmePath;

    public String getReadmePath() {
        return readmePath;
    }
} 