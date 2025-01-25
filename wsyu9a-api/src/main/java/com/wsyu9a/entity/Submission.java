package com.wsyu9a.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    private Long id;
    private Long userId;
    private Long problemId;
    private String flag;
    private Boolean correct;
    private LocalDateTime submitTime;
} 