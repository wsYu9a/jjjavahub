package com.wsyu9a.service.impl;

import com.wsyu9a.dto.AnnouncementDTO;
import com.wsyu9a.entity.Announcement;
import com.wsyu9a.entity.User;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.AnnouncementMapper;
import com.wsyu9a.mapper.UserMapper;
import com.wsyu9a.service.AnnouncementService;
import com.wsyu9a.common.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service  // 这个注解很重要，让Spring能够识别并管理这个bean
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Announcement> getAnnouncements(Integer pageNum, Integer pageSize, String searchKey) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 获取总数
            long total = announcementMapper.countAnnouncements(searchKey);
            
            // 获取当前页数据
            List<Announcement> announcements = announcementMapper.findByPage(searchKey, offset, pageSize);
            
            return new PageResult<>(announcements, total, pageSize, pageNum);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            throw new BusinessException("获取公告列表失败");
        }
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        try {
            Announcement announcement = announcementMapper.findById(id);
            if (announcement == null) {
                throw new BusinessException("公告不存在");
            }
            return announcement;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            throw new BusinessException("获取公告详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Announcement addAnnouncement(AnnouncementDTO announcementDTO, String username) {
        try {
            log.info("开始创建公告: title={}, publisher={}", announcementDTO.getTitle(), username);
            
            // 获取发布者信息
            User publisher = userMapper.findByUsername(username);
            if (publisher == null) {
                log.error("发布者不存在: {}", username);
                throw new BusinessException("发布者不存在");
            }
            
            // 创建公告实体
            Announcement announcement = new Announcement();
            announcement.setTitle(announcementDTO.getTitle());
            announcement.setContent(announcementDTO.getContent());
            announcement.setImportant(announcementDTO.getImportant());
            announcement.setEnabled(announcementDTO.getEnabled());
            announcement.setPublisherId(publisher.getId());
            announcement.setCreateTime(LocalDateTime.now());
            announcement.setUpdateTime(LocalDateTime.now());
            
            // 保存公告
            log.debug("准备保存公告: {}", announcement);
            int rows = announcementMapper.insert(announcement);
            if (rows != 1) {
                log.error("保存公告失败: {}", announcement);
                throw new BusinessException("保存公告失败");
            }
            
            log.info("公告创建成功: id={}, title={}", announcement.getId(), announcement.getTitle());
            return announcement;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建公告异常", e);
            throw new BusinessException("创建公告失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Announcement updateAnnouncement(AnnouncementDTO announcementDTO) {
        try {
            // 检查公告是否存在
            Announcement announcement = announcementMapper.findById(announcementDTO.getId());
            if (announcement == null) {
                throw new BusinessException("公告不存在");
            }

            // 更新公告信息
            announcement.setTitle(announcementDTO.getTitle());
            announcement.setContent(announcementDTO.getContent());
            announcement.setImportant(announcementDTO.getImportant());
            announcement.setEnabled(announcementDTO.getEnabled());
            announcement.setUpdateTime(LocalDateTime.now());

            // 保存更新
            int rows = announcementMapper.update(announcement);
            if (rows != 1) {
                throw new BusinessException("更新公告失败");
            }

            log.info("公告更新成功: {}", announcement.getTitle());
            return announcement;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新公告失败", e);
            throw new BusinessException("更新公告失败，请稍后重试");
        }
    }

    @Override
    public PageResult<Announcement> getPublicAnnouncements(Integer pageNum, Integer pageSize, String searchKey) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 获取总数
            long total = announcementMapper.countPublicAnnouncements(searchKey);
            
            // 获取当前页数据
            List<Announcement> announcements = announcementMapper.findPublicByPage(searchKey, offset, pageSize);
            
            return new PageResult<>(announcements, total, pageSize, pageNum);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            throw new BusinessException("获取公告列表失败");
        }
    }

    @Override
    public Announcement getPublicAnnouncementById(Long id) {
        try {
            Announcement announcement = announcementMapper.findPublicById(id);
            if (announcement == null) {
                throw new BusinessException("公告不存在或已下线");
            }
            return announcement;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取公告详情失败", e);
            throw new BusinessException("获取公告详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncementById(Long id) {
        try {
            int rows = announcementMapper.deleteById(id);
            if (rows != 1) {
                throw new BusinessException("删除公告失败");
            }
            log.info("公告删除成功: {}", id);
        } catch (Exception e) {
            log.error("删除公告失败", e);
            throw new BusinessException("删除公告失败，请稍后重试");
        }
    }
} 