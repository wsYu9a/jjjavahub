package com.wsyu9a.service;

import com.wsyu9a.dto.ProblemDTO;
import com.wsyu9a.dto.ProblemStatusDTO;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.common.PageResult;
import com.wsyu9a.common.Result;
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
} 