package com.wsyu9a.controller.admin.v1;

import com.alibaba.fastjson.JSONObject;
import com.wsyu9a.common.Result;
import com.wsyu9a.entity.Config;
import com.wsyu9a.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminConfigController {
    
    private final ConfigService configService;

    @GetMapping
    public Result<?> getAll() {
        return configService.getConfig();
    }

    @GetMapping("/default")
    public Result<?> getDefault() {
        return configService.getDefault();
    }

    @PostMapping
    public Result<?> updateConfig(@RequestBody String configJson) {
        try {
            if (!StringUtils.hasText(configJson)) {
                return Result.fail(400, "配置数据不能为空");
            }
            Config config = JSONObject.parseObject(configJson, Config.class);
            if (config == null) {
                return Result.fail(400, "配置数据格式错误");
            }
            return configService.updateConfig(config);
        } catch (Exception e) {
            log.error("Error parsing config json: ", e);
            return Result.fail(400, "配置数据解析失败：" + e.getMessage());
        }
    }
} 