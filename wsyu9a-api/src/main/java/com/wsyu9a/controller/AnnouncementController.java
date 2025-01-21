package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.entity.Announcement;
import com.wsyu9a.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Component("publicAnnouncementController")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<PageResult<Announcement>> getPublicAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey) {
        try {
            PageResult<Announcement> pageResult = announcementService.getPublicAnnouncements(pageNum, pageSize, searchKey);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Announcement> getPublicAnnouncementDetail(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getPublicAnnouncementById(id);
            return Result.success("获取成功", announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 