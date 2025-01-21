package com.wsyu9a.controller.admin;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.AnnouncementDTO;
import com.wsyu9a.entity.Announcement;
import com.wsyu9a.service.AnnouncementService;
import com.wsyu9a.annotation.RepeatSubmit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<PageResult<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey) {
        try {
            PageResult<Announcement> pageResult = announcementService.getAnnouncements(pageNum, pageSize, searchKey);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            return Result.success("获取成功", announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/add")
    @RepeatSubmit(interval = 5000)
    public Result<Announcement> addAnnouncement(
            @Validated @RequestBody AnnouncementDTO announcementDTO,
            HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            Announcement announcement = announcementService.addAnnouncement(announcementDTO, username);
            return Result.success("发布成功", announcement);
        } catch (Exception e) {
            log.error("发布公告失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/update")
    @RepeatSubmit(interval = 5000)
    public Result<Announcement> updateAnnouncement(
            @Validated @RequestBody AnnouncementDTO announcementDTO) {
        try {
            Announcement announcement = announcementService.updateAnnouncement(announcementDTO);
            return Result.success("更新成功", announcement);
        } catch (Exception e) {
            log.error("更新公告失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 