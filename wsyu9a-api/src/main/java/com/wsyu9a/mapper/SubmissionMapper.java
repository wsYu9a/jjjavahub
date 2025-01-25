package com.wsyu9a.mapper;

import com.wsyu9a.entity.Submission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubmissionMapper {

    /**
     * 插入提交记录
     */
    @Insert("INSERT INTO submission (" +
            "user_id, problem_id, flag, correct, submit_time" +
            ") VALUES (" +
            "#{userId}, #{problemId}, #{flag}, #{correct}, #{submitTime}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Submission submission);
    
    /**
     * 检查用户是否已经解决过该题目
     */
    @Select("SELECT EXISTS (" +
            "SELECT 1 FROM submission s " +
            "JOIN sys_user u ON s.user_id = u.id " +
            "WHERE u.username = #{username} " +
            "AND s.problem_id = #{problemId} " +
            "AND s.correct = true" +
            ")")
    boolean hasSolved(@Param("username") String username, @Param("problemId") Long problemId);

    /**
     * 获取题目的提交次数
     */
    @Select("SELECT COUNT(*) FROM submission WHERE problem_id = #{problemId}")
    int getSubmitCount(@Param("problemId") Long problemId);

    /**
     * 获取题目的解决人数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM submission " +
            "WHERE problem_id = #{problemId} AND correct = true")
    int getSolvedCount(@Param("problemId") Long problemId);

    /**
     * 获取题目的通过率
     */
    @Select("SELECT ROUND(" +
            "IFNULL(" +
                "(SELECT COUNT(*) FROM submission " +
                "WHERE problem_id = #{problemId} AND correct = true) * 100.0 / " +
                "NULLIF((SELECT COUNT(*) FROM submission WHERE problem_id = #{problemId}), 0)" +
            ", 0)" +
            ", 2)")
    double getPassRate(@Param("problemId") Long problemId);
} 