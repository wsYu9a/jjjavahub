package com.wsyu9a.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface AdminDashboardMapper {

    @Select("SELECT " +
            "(SELECT COUNT(*) FROM sys_user WHERE deleted = 0) AS userCount, " +
            "(SELECT COUNT(*) FROM problem) AS problemCount, " +
            "(SELECT COUNT(*) FROM problem_category) AS categoryCount, " +
            "(SELECT COUNT(*) FROM announcement) AS announcementCount")
    Map<String, Object> getDashboardStats();
} 