package com.wsyu9a.controller;

import com.wsyu9a.service.AdminDashboardService;
import com.wsyu9a.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/api/admin/dashboard/stats")
    public Result getDashboardStats() {
        Map<String, Object> stats = adminDashboardService.getDashboardStats();
        return Result.success("获取成功", stats);
    }
} 