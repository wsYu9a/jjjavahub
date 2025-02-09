package com.wsyu9a.service;

import com.wsyu9a.dto.ProblemDTO;
import com.wsyu9a.dto.ProblemStatusDTO;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.common.Result;
import com.wsyu9a.vo.ProblemVO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ProblemService {
    PageResult<Problem> getProblems(Integer pageNum, Integer pageSize, String searchKey, Long categoryId, String difficulty);
    Problem getProblemById(Long id);
    Problem addProblem(ProblemDTO problemDTO);
    Problem updateProblem(ProblemDTO problemDTO);
    void deleteProblem(Long id);
    String uploadDockerCompose(MultipartFile file, Long problemId);
    void updateProblemStatus(ProblemStatusDTO statusDTO);
    Result<String> uploadReadme(MultipartFile file, MultipartFile[] assets);

    /**
     * 获取公开的题目列表（用户端）
     */
    PageResult<Problem> getPublicProblems(Integer pageNum, Integer pageSize, 
        String searchKey, Long categoryId, String difficulty);

    /**
     * 获取公开的题目详情（用户端）
     */
    Problem getPublicProblemById(Long id);

    /**
     * 获取题目的 README 内容
     * @param path README 文件路径
     * @return README 文件内容
     * @throws IOException 如果文件读取失败
     */
    String getReadmeContent(String path) throws IOException;

    /**
     * 提交flag
     * @param username 用户名
     * @param problemId 题目ID
     * @param flag 提交的flag
     * @return 是否正确
     */
    boolean submitFlag(String username, Long problemId, String flag);

    /**
     * 获取用户题目列表
     * @param username 用户名
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param searchKey 搜索关键字
     * @param categoryId 分类ID
     * @param difficulty 难度
     * @return 分页结果
     */
    PageResult<ProblemVO> getUserProblems(String username, Integer pageNum, Integer pageSize,
            String searchKey, Long categoryId, String difficulty);
} 