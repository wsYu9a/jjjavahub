package com.wsyu9a.service;

import com.wsyu9a.common.Result;
import com.wsyu9a.entity.Config;

public interface ConfigService {
    Result<?> getConfig();
    Result<?> getDefault();
    Result<?> updateConfig(Config config);
} 