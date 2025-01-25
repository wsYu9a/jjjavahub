package com.wsyu9a.service.impl;

import com.wsyu9a.dto.SolveRecordDTO;
import com.wsyu9a.mapper.SubmissionMapper;
import com.wsyu9a.service.SolveRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolveRecordServiceImpl implements SolveRecordService {

    private final SubmissionMapper submissionMapper;

    @Override
    public List<SolveRecordDTO> getLatestSolveRecords() {
        return submissionMapper.getLatestSolveRecords();
    }
} 