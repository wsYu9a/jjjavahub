package com.wsyu9a.service;

import com.wsyu9a.dto.AnnouncementDTO;
import com.wsyu9a.entity.Announcement;
import com.wsyu9a.common.PageResult;

public interface AnnouncementService {
    /**
     * 分页获取公告列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param searchKey 搜索关键词
     * @return 分页结果
     */
    PageResult<Announcement> getAnnouncements(Integer pageNum, Integer pageSize, String searchKey);

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    Announcement getAnnouncementById(Long id);

    /**
     * 添加公告
     *
     * @param announcementDTO 公告信息
     * @param username 发布者用户名
     * @return 创建的公告
     */
    Announcement addAnnouncement(AnnouncementDTO announcementDTO, String username);

    /**
     * 更新公告
     *
     * @param announcementDTO 公告信息
     * @return 更新后的公告
     */
    Announcement updateAnnouncement(AnnouncementDTO announcementDTO);

    /**
     * 获取公开的公告列表（用户端）
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param searchKey 搜索关键词
     * @return 分页结果
     */
    PageResult<Announcement> getPublicAnnouncements(Integer pageNum, Integer pageSize, String searchKey);

    /**
     * 获取公开的公告详情（用户端）
     *
     * @param id 公告ID
     * @return 公告详情
     */
    Announcement getPublicAnnouncementById(Long id);
} 