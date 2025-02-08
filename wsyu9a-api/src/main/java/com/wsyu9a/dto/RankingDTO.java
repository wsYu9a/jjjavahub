package com.wsyu9a.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {
    private Integer rank;        // 排名
    private String username;     // 用户名
    private String avatar;     // 用户名
    private Integer score;       // 分数
    private Integer solvedCount; // 解题数量
} 