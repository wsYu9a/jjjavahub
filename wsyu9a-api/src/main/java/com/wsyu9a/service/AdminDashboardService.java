package com.wsyu9a.service;

import com.wsyu9a.mapper.AdminDashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminDashboardService {

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    public Map<String, Object> getDashboardStats() {
        return adminDashboardMapper.getDashboardStats();
    }
} 