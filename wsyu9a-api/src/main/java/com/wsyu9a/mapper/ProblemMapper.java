package com.wsyu9a.mapper;

import com.wsyu9a.entity.Problem;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProblemMapper {
    
    @Select("<script>" +
            "SELECT p.*, c.name as category_name FROM problem p " +
            "LEFT JOIN problem_category c ON p.category_id = c.id " +
            "WHERE 1=1 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (p.title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR p.summary LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND p.category_id = #{categoryId} " +
            "</if>" +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Problem> findByPage(@Param("searchKey") String searchKey,
                           @Param("categoryId") Long categoryId,
                           @Param("offset") Integer offset,
                           @Param("pageSize") Integer pageSize);
    
    @Select("SELECT * FROM problem WHERE title = #{title}")
    Problem findByTitle(String title);
    
    @Select("SELECT * FROM problem WHERE id = #{id}")
    Problem findById(Long id);
    
    @Insert("INSERT INTO problem(title, summary, detail, flag, score, difficulty, " +
            "category_id, docker_compose_path, enabled, create_time, update_time) " +
            "VALUES(#{title}, #{summary}, #{detail}, #{flag}, #{score}, #{difficulty}, " +
            "#{categoryId}, #{dockerComposePath}, #{enabled}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Problem problem);
    
    @Update("UPDATE problem SET title = #{title}, summary = #{summary}, " +
            "detail = #{detail}, flag = #{flag}, score = #{score}, " +
            "difficulty = #{difficulty}, category_id = #{categoryId}, " +
            "docker_compose_path = #{dockerComposePath}, enabled = #{enabled}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int update(Problem problem);
    
    @Delete("DELETE FROM problem WHERE id = #{id}")
    int delete(Long id);
    
    @Update("UPDATE problem SET enabled = #{enabled}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                    @Param("enabled") Boolean enabled,
                    @Param("updateTime") LocalDateTime updateTime);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM problem WHERE 1=1 " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR summary LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId} " +
            "</if>" +
            "</script>")
    long countProblems(@Param("searchKey") String searchKey,
                      @Param("categoryId") Long categoryId);

    @Select("<script>" +
            "SELECT COUNT(*) FROM problem " +
            "WHERE enabled = true " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR summary LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId} " +
            "</if>" +
            "<if test='difficulty != null and difficulty != \"\"'>" +
            "AND difficulty = #{difficulty}" +
            "</if>" +
            "</script>")
    long countPublicProblems(
        @Param("searchKey") String searchKey,
        @Param("categoryId") Long categoryId,
        @Param("difficulty") String difficulty);

    @Select("<script>" +
            "SELECT p.*, pc.name as category_name " +
            "FROM problem p " +
            "LEFT JOIN problem_category pc ON p.category_id = pc.id " +
            "WHERE p.enabled = true " +
            "<if test='searchKey != null and searchKey != \"\"'>" +
            "AND (p.title LIKE CONCAT('%',#{searchKey},'%') " +
            "OR p.summary LIKE CONCAT('%',#{searchKey},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND p.category_id = #{categoryId} " +
            "</if>" +
            "<if test='difficulty != null and difficulty != \"\"'>" +
            "AND p.difficulty = #{difficulty}" +
            "</if>" +
            "ORDER BY p.id " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Problem> findPublicByPage(
        @Param("searchKey") String searchKey,
        @Param("categoryId") Long categoryId,
        @Param("difficulty") String difficulty,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize);

    @Select("SELECT p.*, pc.name as category_name " +
            "FROM problem p " +
            "LEFT JOIN problem_category pc ON p.category_id = pc.id " +
            "WHERE p.id = #{id} AND p.enabled = true")
    Problem findPublicById(@Param("id") Long id);
} 