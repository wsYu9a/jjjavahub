package com.wsyu9a.mapper;

import com.wsyu9a.entity.Config;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ConfigMapper {
    
    @Select("SELECT * FROM sys_config WHERE id = 2")
    Config getConfig();
    
    @Select("SELECT * FROM sys_config WHERE id = 1")
    Config getDefaultConfig();
    
    @Update("UPDATE sys_config SET " +
            "docker_api = #{dockerApi}, " +
            "docker_registry = #{dockerRegistry}, " +
            "docker_max_port = #{dockerMaxPort}, " +
            "docker_min_port = #{dockerMinPort}, " +
            "docker_time = #{dockerTime}, " +
            "update_time = NOW() " +
            "WHERE id = 2")
    int updateConfig(Config config);
} 