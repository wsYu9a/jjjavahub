package com.wsyu9a.entity;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Data
@Entity
@Table(name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String flag;

    @Column(nullable = false)
    private String summary;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "docker_compose_path")
    private String dockerComposePath;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    // 新增的category_name字段
    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "submit_count", nullable = false)
    private Integer submitCount;

    @Column(name = "solved_count", nullable = false)
    private Integer solvedCount;

    @Column(name = "pass_rate", nullable = false)
    private String passRate;
} 