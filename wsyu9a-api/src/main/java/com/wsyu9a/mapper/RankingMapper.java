package com.wsyu9a.mapper;

import com.wsyu9a.dto.RankingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RankingMapper {

    @Select("SELECT * FROM ranking ORDER BY score DESC")
    List<RankingDTO> getRankingList();

    @Select("SELECT u.username, u.score, u.avatar, " +
            "(SELECT COUNT(*) FROM submission s WHERE s.user_id = u.id AND s.correct = true) as solved_count " +
            "FROM sys_user u " +
            "WHERE u.deleted = 0 AND u.enabled = true " +
            "ORDER BY u.score DESC, solved_count DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<RankingDTO> findRankingByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM sys_user WHERE deleted = 0 AND enabled = true")
    long countTotalRankings();
} 