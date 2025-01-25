package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.dto.RankingDTO;
import com.wsyu9a.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public Result<List<RankingDTO>> getRankingList() {
        try {
            List<RankingDTO> rankings = rankingService.getRankingList();
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取排行榜失败", e);
            return Result.fail("获取排行榜失败");
        }
    }
} 