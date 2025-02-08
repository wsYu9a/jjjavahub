package com.wsyu9a.service.impl;

import com.wsyu9a.dto.ProblemDTO;
import com.wsyu9a.dto.ProblemStatusDTO;
import com.wsyu9a.entity.Problem;
import com.wsyu9a.entity.Category;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.ProblemMapper;
import com.wsyu9a.mapper.CategoryMapper;
import com.wsyu9a.service.ProblemService;
import com.wsyu9a.common.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import com.wsyu9a.dto.UploadResult;
import org.springframework.util.FileSystemUtils;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import com.wsyu9a.common.Result;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import com.wsyu9a.entity.Submission;
import com.wsyu9a.mapper.SubmissionMapper;
import com.wsyu9a.service.DockerService;
import com.wsyu9a.dto.docker.DockerEnvDTO;
import com.wsyu9a.mapper.UserMapper;
import com.wsyu9a.entity.User;
import com.wsyu9a.vo.ProblemVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemMapper problemMapper;
    private final CategoryMapper categoryMapper;
    private final SubmissionMapper submissionMapper;
    private final DockerService dockerService;
    private final UserMapper userMapper;

    @Value("${problem.docker-compose.upload-path}")
    private String uploadPath;

    @Value("${problem.upload.readme-path}")
    private String readmePath;

    @Value("${problem.upload.attachment-path}")
    private String fujianPath;

    @PostConstruct
    public void init() {
        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录成功: {}", uploadPath);
            }

            // 创建README上传目录
            Path readmeDir = Paths.get(readmePath);
            if (!Files.exists(readmeDir)) {
                Files.createDirectories(readmeDir);
                log.info("创建README上传目录成功: {}", readmePath);
            }
        } catch (Exception e) {
            log.error("创建上传目录失败", e);
            throw new RuntimeException("创建上传目录失败", e);
        }
    }

    public Result<String> uploadReadme(MultipartFile file, MultipartFile[] assets) {
        try {
            // 检查主文件
            if (file.isEmpty()) {
                return Result.fail("README文件不能为空");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".md")) {
                return Result.fail("主文件必须是 markdown 格式");
            }

            // 生成唯一目录名
            String dirName = UUID.randomUUID().toString();
            Path uploadDir = Paths.get(readmePath, dirName);
            Files.createDirectories(uploadDir);

            // 保存 README.md
            Path readmePath = uploadDir.resolve("README.md");
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, readmePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 处理资源文件
            if (assets != null && assets.length > 0) {
                Path assetsDir = uploadDir.resolve("assets");
                Files.createDirectories(assetsDir);

                for (MultipartFile asset : assets) {
                    if (asset.isEmpty()) continue;

                    String filename = asset.getOriginalFilename();
                    if (filename == null || filename.isEmpty()) continue;

                    // 检查文件类型
                    String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
                    if (!isAllowedImageType(extension)) {
                        // 删除已上传的文件
                        FileSystemUtils.deleteRecursively(uploadDir);
                        return Result.fail("不支持的图片格式: " + filename);
                    }

                    // 保存图片文件
                    Path assetPath = assetsDir.resolve(filename);
                    try (InputStream inputStream = asset.getInputStream()) {
                        Files.copy(inputStream, assetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            String relativePath = dirName + "/README.md";
            return Result.success("上传成功", relativePath);

        } catch (Exception e) {
            return Result.fail("上传失败，请稍后重试");
        }
    }

    private boolean isAllowedImageType(String extension) {
        // 检查支持的图片格式
        return extension.equals(".jpg") || extension.equals(".png") || extension.equals(".gif");
    }

    @Override
    public PageResult<Problem> getProblems(Integer pageNum, Integer pageSize, String searchKey, Long categoryId, String difficulty) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 获取总数
            long total = problemMapper.countProblems(searchKey, categoryId);
            
            // 获取当前页数据
            List<Problem> problems = problemMapper.findByPage(searchKey, categoryId, difficulty, offset, pageSize);
            
            return new PageResult<>(problems, total, pageSize, pageNum);
        } catch (Exception e) {
            log.error("查询题目列表失败", e);
            throw new BusinessException("获取题目列表失败");
        }
    }

    @Override
    public Problem getProblemById(Long id) {
        try {
            Problem problem = problemMapper.findById(id);
            if (problem == null) {
                throw new BusinessException("题目不存在");
            }
            return problem;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取题目详情失败", e);
            throw new BusinessException("获取题目详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Problem addProblem(ProblemDTO problemDTO) {
        try {
            // 检查题目标题是否已存在
            Problem existingProblem = problemMapper.findByTitle(problemDTO.getTitle());
            if (existingProblem != null) {
                throw new BusinessException("题目标题已存在");
            }

            Problem problem = new Problem();
            BeanUtils.copyProperties(problemDTO, problem);
            
            // 确保设置这些路径
            problem.setDockerComposePath(problemDTO.getDockerComposePath());
            problem.setAttachmentPath(problemDTO.getAttachmentPath());
            
            // 添加日志
            log.info("Problem data before insert: {}", problem);
            log.info("Attachment path: {}", problem.getAttachmentPath());
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            problem.setCreateTime(now);
            problem.setUpdateTime(now);

            // 设置默认值
            problem.setEnabled(true);

            int rows = problemMapper.insert(problem);
            if (rows != 1) {
                throw new BusinessException("添加题目失败");
            }

            // 添加日志
            log.info("Problem inserted successfully with id: {}", problem.getId());
            log.info("Final problem data: {}", problem);

            return problem;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加题目失败", e);
            throw new BusinessException("添加题目失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Problem updateProblem(ProblemDTO problemDTO) {
        try {
            // 检查题目是否存在
            Problem problem = problemMapper.findById(problemDTO.getId());
            if (problem == null) {
                throw new BusinessException("题目不存在");
            }

            // 检查分类是否存在
            if (categoryMapper.findById(problemDTO.getCategoryId()) == null) {
                throw new BusinessException("所选分类不存在");
            }

            // 更新题目信息
            problem.setTitle(problemDTO.getTitle());
            problem.setSummary(problemDTO.getSummary());
            problem.setDetail(problemDTO.getDetail());
            problem.setFlag(problemDTO.getFlag());
            problem.setScore(problemDTO.getScore());
            problem.setDifficulty(problemDTO.getDifficulty());
            problem.setCategoryId(problemDTO.getCategoryId());
            problem.setDockerComposePath(problemDTO.getDockerComposePath());
            problem.setAttachmentPath(problemDTO.getAttachmentPath());
            problem.setEnabled(problemDTO.getEnabled());
            problem.setUpdateTime(LocalDateTime.now());

            // 添加日志
            log.info("Updating problem with data: {}", problem);

            // 保存更新
            int rows = problemMapper.update(problem);
            if (rows != 1) {
                throw new BusinessException("更新题目失败");
            }

            log.info("题目更新成功: {}", problem.getTitle());
            return problem;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新题目失败", e);
            throw new BusinessException("更新题目失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProblem(Long id) {
        try {
            // 检查题目是否存在
            Problem problem = problemMapper.findById(id);
            if (problem == null) {
                throw new BusinessException("题目不存在");
            }

            // 删除题目
            int rows = problemMapper.delete(id);
            if (rows != 1) {
                throw new BusinessException("删除题目失败");
            }

            // 如果存在docker-compose文件，也需要删除
            if (problem.getDockerComposePath() != null) {
                Path filePath = Paths.get(problem.getDockerComposePath());
                Files.deleteIfExists(filePath);
            }

            log.info("题目删除成功: {}", problem.getTitle());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除题目失败", e);
            throw new BusinessException("删除题目失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadDockerCompose(MultipartFile file, Long problemId) {
        try {
            log.debug("开始处理文件上传: problemId={}, fileName={}, fileSize={}", 
                    problemId, file.getOriginalFilename(), file.getSize());
            
            // 检查题目是否存在
            Problem problem = problemMapper.findById(problemId);
            if (problem == null) {
                throw new BusinessException("题目不存在");
            }

            // 检查文件是否为空
            if (file.isEmpty()) {
                throw new BusinessException("文件不能为空");
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".yml") && !originalFilename.endsWith(".yaml"))) {
                throw new BusinessException("只能上传 yml 或 yaml 文件");
            }

            // 创建题目专属目录
            String problemDir = problemId.toString();
//            String problemDir = UUID.randomUUID().toString();
            Path problemPath = Paths.get(uploadPath, problemDir);
            if (!Files.exists(problemPath)) {
                Files.createDirectories(problemPath);
                log.debug("创建题目目录: {}", problemPath);
            }

            // 保存文件
            String filename = "docker-compose.yml";
            Path filePath = problemPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("文件保存成功: {}", filePath);

            // 更新题目的 docker-compose 文件路径
            String relativePath = problemDir + "/" + filename;
            problem.setDockerComposePath(relativePath);
            problem.setUpdateTime(LocalDateTime.now());
            problemMapper.update(problem);
            log.debug("更新题目文件路径成功: {}", relativePath);

            return relativePath;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传Docker Compose文件失败", e);
            throw new BusinessException("上传失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProblemStatus(ProblemStatusDTO statusDTO) {
        try {
            // 检查题目是否存在
            if (problemMapper.findById(statusDTO.getProblemId()) == null) {
                throw new BusinessException("题目不存在");
            }

            // 更新状态
            int rows = problemMapper.updateStatus(
                statusDTO.getProblemId(), 
                statusDTO.getEnabled(), 
                LocalDateTime.now()
            );
            
            if (rows != 1) {
                throw new BusinessException("更新题目状态失败");
            }

            log.info("题目状态更新成功, id: {}, enabled: {}", 
                    statusDTO.getProblemId(), statusDTO.getEnabled());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新题目状态失败", e);
            throw new BusinessException("更新题目状态失败，请稍后重试");
        }
    }

    @Override
    public PageResult<Problem> getPublicProblems(Integer pageNum, Integer pageSize,
            String searchKey, Long categoryId, String difficulty) {
        try {
            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;
            
            // 获取总数
            long total = problemMapper.countPublicProblems(searchKey, categoryId, difficulty);
            
            // 获取当前页数据
            List<Problem> problems = problemMapper.findPublicByPage(
                searchKey, categoryId, difficulty, offset, pageSize);
            
            return new PageResult<>(problems, total, pageSize, pageNum);
        } catch (Exception e) {
            log.error("获取题目列表失败", e);
            throw new BusinessException("获取题目列表失败");
        }
    }

    @Override
    public Problem getPublicProblemById(Long id) {
        try {
            Problem problem = problemMapper.findPublicById(id);
            if (problem == null) {
                throw new BusinessException("题目不存在或未发布");
            }
            return problem;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取题目详情失败", e);
            throw new BusinessException("获取题目详情失败");
        }
    }

    public String getReadmeContent(String path) throws IOException {
        String newPath = path.replaceFirst("readme/", "");
        Path filePath = Paths.get(readmePath, newPath);
        if (!Files.exists(filePath)) {
            throw new BusinessException("README文件不存在");
        }
        return Files.readString(filePath);
    }

    public String getFujianContent(String path) throws IOException {
        String newPath = path.replaceFirst("fujian/", "");
        Path filePath = Paths.get(fujianPath, newPath);
        if (!Files.exists(filePath)) {
            throw new BusinessException("附件不存在");
        }
        return Files.readString(filePath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFlag(String username, Long problemId, String flag) {
        log.info("开始处理提交: username={}, problemId={}", username, problemId);
        
        // 获取用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new BusinessException("用户不存在");
        }
        log.info("用户信息: id={}, username={}", user.getId(), user.getUsername());

        // 获取题目信息
        Problem problem = problemMapper.findById(problemId);
        if (problem == null) {
            log.warn("题目不存在: {}", problemId);
            throw new BusinessException("题目不存在");
        }
        log.info("题目信息: id={}, title={}", problem.getId(), problem.getTitle());

        // 获取容器环境变量中的flag
        DockerEnvDTO env = dockerService.getEnvironmentStatus(problemId);
        if (env == null || !"running".equals(env.getStatus())) {
            log.warn("题目环境未运行: problemId={}, envStatus={}", problemId, env != null ? env.getStatus() : "null");
            throw new BusinessException("请先启动题目环境");
        }
        log.info("环境状态: problemId={}, status={}", problemId, env.getStatus());

        // 验证flag
        String envFlag = env.getFlag();
        if (envFlag == null) {
            log.error("题目 {} 的环境变量中没有flag", problemId);
            throw new BusinessException("题目环境异常，请联系管理员");
        }

        // 检查flag是否正确
        boolean isCorrect = envFlag.equals(flag);
        log.info("Flag验证结果: problemId={}, isCorrect={}", problemId, isCorrect);

        // 如果flag正确，先检查是否已经解决过
        if (isCorrect) {
            boolean hasSolved = submissionMapper.hasSolved(username, problemId);
            log.info("检查是否已解决: username={}, problemId={}, hasSolved={}", username, problemId, hasSolved);
            if (hasSolved) {
                throw new BusinessException("你已经解决过这道题目了");
            }
        }

        // 创建提交记录
        Submission submission = new Submission();
        submission.setUserId(user.getId());
        submission.setProblemId(problemId);
        submission.setFlag(flag);
        submission.setSubmitTime(LocalDateTime.now());
        submission.setCorrect(isCorrect);
        
        // 保存提交记录并更新题目统计
        log.info("保存提交记录: userId={}, problemId={}, correct={}", user.getId(), problemId, isCorrect);
        submissionMapper.insert(submission);
        log.info("提交记录已保存: submissionId={}", submission.getId());
        
        log.info("更新题目提交次数: problemId={}", problemId);
        problemMapper.updateStatistics(problemId);  // 增加提交次数

        // 如果flag正确，更新用户积分和解决次数
        if (isCorrect) {
            Integer currentScore = user.getScore() != null ? user.getScore() : 0;
            Integer problemScore = problem.getScore() != null ? problem.getScore() : 0;
            log.info("更新用户积分: userId={}, oldScore={}, addScore={}", user.getId(), currentScore, problemScore);
            userMapper.updateScore(user.getId(), currentScore + problemScore);
            
            log.info("更新题目解决次数: problemId={}", problemId);
            problemMapper.updateStatistics2(problemId);  // 增加解决次数
        }

        log.info("提交处理完成: username={}, problemId={}, isCorrect={}", username, problemId, isCorrect);
        return isCorrect;
    }

    @Override
    public PageResult<ProblemVO> getUserProblems(String username, Integer pageNum, Integer pageSize,
            String searchKey, Long categoryId, String difficulty) {
        log.info("获取用户题目列表: username={}, pageNum={}, pageSize={}", username, pageNum, pageSize);
        
        int offset = (pageNum - 1) * pageSize;
        
        List<ProblemVO> problems = problemMapper.findUserProblems(offset, pageSize, searchKey, categoryId, difficulty);
        log.info("查询到题目数量: {}", problems.size());
        
        List<Long> solvedProblemIds = submissionMapper.findSolvedProblemIds(username);
        log.info("用户已解决题目数量: {}", solvedProblemIds.size());
        
        problems.forEach(problem -> {
            boolean solved = solvedProblemIds.contains(problem.getId());
            problem.setSolved(solved);
            log.debug("题目 {} solved状态: {}", problem.getId(), solved);
        });
        
        int total = problemMapper.countUserProblems(searchKey, categoryId, difficulty);
        
        return new PageResult<>(problems, total, pageSize, pageNum);
    }
} 