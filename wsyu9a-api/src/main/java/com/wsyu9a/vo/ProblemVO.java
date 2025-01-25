package com.wsyu9a.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProblemVO {
    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 题目简介
     */
    private String summary;

    /**
     * 提交次数
     */
    private Integer submitCount;

    /**
     * 解决人数
     */
    private Integer solvedCount;

    /**
     * 通过率
     */
    private Double passRate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 当前用户是否已解决
     */
    private Boolean solved;
} 