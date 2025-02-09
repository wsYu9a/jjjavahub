package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.RankingDTO;
import com.wsyu9a.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking")
    public Result<PageResult<RankingDTO>> getRankingList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageResult<RankingDTO> pageResult = rankingService.getRankingList(pageNum, pageSize);
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取排行榜失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 