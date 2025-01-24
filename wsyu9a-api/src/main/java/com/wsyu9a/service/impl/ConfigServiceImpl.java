package com.wsyu9a.service.impl;

import com.wsyu9a.common.Result;
import com.wsyu9a.entity.Config;
import com.wsyu9a.mapper.ConfigMapper;
import com.wsyu9a.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper ConfigMapper;

    @Override
    public Result<?> getConfig() {
        try {
            Config config = ConfigMapper.getConfig();
            if (config == null) {
                log.warn("No configuration found in database");
                return Result.fail(404, "未找到配置信息");
            }
            return Result.success("获取配置成功", config);
        } catch (Exception e) {
            log.error("Error getting config: ", e);
            return Result.fail(500, "获取配置失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> getDefault() {
        try {
            Config config = ConfigMapper.getDefaultConfig();
            if (config == null) {
                log.warn("No default configuration found in database");
                return Result.fail(404, "未找到默认配置信息");
            }
            return Result.success("获取默认配置成功", config);
        } catch (Exception e) {
            log.error("Error getting default config: ", e);
            return Result.fail(500, "获取默认配置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<?> updateConfig(Config config) {
        try {
            if (config == null || config.getId() == null) {
                return Result.fail(400, "配置信息不能为空");
            }
            int rows = ConfigMapper.updateConfig(config);
            if (rows > 0) {
                Config updatedConfig = ConfigMapper.getConfig();
                return Result.success("配置更新成功", updatedConfig);
            }
            return Result.fail(500, "配置更新失败");
        } catch (Exception e) {
            log.error("Error updating config: ", e);
            return Result.fail(500, "配置更新失败：" + e.getMessage());
        }
    }
} 