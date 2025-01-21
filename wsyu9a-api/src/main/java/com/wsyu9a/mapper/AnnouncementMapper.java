package com.wsyu9a.mapper;

import com.wsyu9a.entity.Announcement;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AnnouncementMapper {
    
    @Select("<script>" +
            "SELECT a.*, u.username as publisher_name FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE 1=1 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (a.title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR a.content LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "ORDER BY a.important DESC, a.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Announcement> findByPage(@Param("searchKey") String searchKey,
                                @Param("offset") Integer offset,
                                @Param("pageSize") Integer pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM announcement WHERE 1=1 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR content LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "</script>")
    long countAnnouncements(@Param("searchKey") String searchKey);
    
    @Insert("INSERT INTO announcement(title, content, important, enabled, " +
            "publisher_id, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{important}, #{enabled}, " +
            "#{publisherId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);
    
    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Announcement findById(Long id);
    
    @Update("UPDATE announcement SET title = #{title}, content = #{content}, " +
            "important = #{important}, enabled = #{enabled}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int update(Announcement announcement);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM announcement " +
            "WHERE enabled = true " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR content LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "</script>")
    long countPublicAnnouncements(@Param("searchKey") String searchKey);
    
    @Select("<script>" +
            "SELECT a.*, u.username as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.enabled = true " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (a.title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR a.content LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "ORDER BY a.important DESC, a.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Announcement> findPublicByPage(
            @Param("searchKey") String searchKey,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
    
    @Select("SELECT a.*, u.username as publisher_name " +
            "FROM announcement a " +
            "LEFT JOIN sys_user u ON a.publisher_id = u.id " +
            "WHERE a.id = #{id} AND a.enabled = true")
    Announcement findPublicById(@Param("id") Long id);
} 