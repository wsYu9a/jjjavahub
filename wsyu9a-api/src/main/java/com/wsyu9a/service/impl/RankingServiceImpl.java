package com.wsyu9a.service.impl;

import com.wsyu9a.dto.RankingDTO;
import com.wsyu9a.mapper.UserMapper;
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

    private final UserMapper userMapper;

    @Override
    public List<RankingDTO> getRankingList() {
        AtomicInteger rank = new AtomicInteger(1);
        return userMapper.getRankingList().stream()
            .map(dto -> {
                dto.setRank(rank.getAndIncrement());
                return dto;
            })
            .collect(Collectors.toList());
    }
} 