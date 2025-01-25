package com.wsyu9a.service;

import com.wsyu9a.dto.SolveRecordDTO;
import java.util.List;

public interface SolveRecordService {
    List<SolveRecordDTO> getLatestSolveRecords();
} 