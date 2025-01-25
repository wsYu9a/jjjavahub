package com.wsyu9a.controller;

import com.wsyu9a.common.Result;
import com.wsyu9a.dto.SolveRecordDTO;
import com.wsyu9a.service.SolveRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/solve-records")
@RequiredArgsConstructor
public class SolveRecordController {

    private final SolveRecordService solveRecordService;

    @GetMapping("/latest")
    public Result<List<SolveRecordDTO>> getLatestSolveRecords() {
        try {
            List<SolveRecordDTO> records = solveRecordService.getLatestSolveRecords();
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取最新解题记录失败", e);
            return Result.fail("获取解题动态失败");
        }
    }
} 