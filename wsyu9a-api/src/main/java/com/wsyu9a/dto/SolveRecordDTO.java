package com.wsyu9a.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolveRecordDTO {
    private String username;      // 用户名
    private Long problemId;       // 题目ID
    private String problemTitle;  // 题目名称
    private LocalDateTime solveTime;  // 解题时间
} 