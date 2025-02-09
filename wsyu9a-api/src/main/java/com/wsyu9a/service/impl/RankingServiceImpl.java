package com.wsyu9a.service.impl;

import com.wsyu9a.common.PageResult;
import com.wsyu9a.dto.RankingDTO;
import com.wsyu9a.mapper.RankingMapper;
import com.wsyu9a.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingMapper rankingMapper;

    @Override
    public List<RankingDTO> getRankingList() {
        return getRankingList(1, Integer.MAX_VALUE).getRecords(); // 获取所有记录
    }

    @Override
    public PageResult<RankingDTO> getRankingList(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<RankingDTO> rankings = rankingMapper.findRankingByPage(offset, pageSize);
        long total = rankingMapper.countTotalRankings();

        // 计算排名
        AtomicInteger rankCounter = new AtomicInteger(offset + 1);
        rankings.forEach(dto -> dto.setRank(rankCounter.getAndIncrement()));

        return new PageResult<>(rankings, total, pageSize, pageNum);
    }
} 