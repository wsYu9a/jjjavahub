package com.wsyu9a.service;

import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.RankingDTO;
import java.util.List;

public interface RankingService {
    List<RankingDTO> getRankingList();

    PageResult<RankingDTO> getRankingList(int pageNum, int pageSize);
} 