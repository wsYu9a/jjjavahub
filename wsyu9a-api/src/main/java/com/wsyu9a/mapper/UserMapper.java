package com.wsyu9a.mapper;

import com.wsyu9a.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
    
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    User findByEmail(String email);
    
    @Insert("INSERT INTO sys_user(username, email, password, role, enabled, create_time, update_time) " +
            "VALUES(#{username}, #{email}, #{password}, #{role}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE sys_user SET password = #{user.password}, update_time = #{user.updateTime} " +
            "WHERE id = #{user.id} AND deleted = 0")
    int updateById(@Param("user") User user);
    
    @Select("SELECT * FROM sys_user WHERE deleted = 0")
    List<User> findAllUsers();
    
    @Select("SELECT * FROM sys_user WHERE id = #{id} AND deleted = 0")
    User findById(Long id);
    
    @Update("UPDATE sys_user SET enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    void updateStatus(User user);
    
    @Update("UPDATE sys_user SET role = #{role}, update_time = #{updateTime} WHERE id = #{id}")
    void updateRole(User user);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM sys_user WHERE deleted = 0 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (username LIKE CONCAT('%',#{searchKey},'%') " +
            "OR email LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "</script>")
    long countUsers(@Param("searchKey") String searchKey);
    
    @Select("<script>" +
            "SELECT * FROM sys_user WHERE deleted = 0 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (username LIKE CONCAT('%',#{searchKey},'%') " +
            "OR email LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<User> findUsersByPage(@Param("searchKey") String searchKey, 
                              @Param("offset") Integer offset, 
                              @Param("pageSize") Integer pageSize);
} 