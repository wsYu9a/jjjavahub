package com.wsyu9a.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserStatsDTO {
    private String username;            // 用户名
    private String role;
    private String avatar;
    private LocalDateTime joinTime;    // 加入时间
    private Integer totalScore;        // 总积分
    private Integer ranking;           // 排名
    private Integer solvedCount;       // 解题数量
    private Integer submitCount;       // 提交次数
    private Map<String, Integer> difficultyStats;  // 各难度解题数量
    private Map<String, Integer> categoryStats;    // 各分类解题数量
    private List<SolveRecordDTO> recentSolves;    // 最近解题记录
} 