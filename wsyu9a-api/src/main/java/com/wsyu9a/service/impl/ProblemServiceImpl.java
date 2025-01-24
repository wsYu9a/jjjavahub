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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemMapper problemMapper;
    private final CategoryMapper categoryMapper;

    @Value("${problem.docker-compose.upload-path}")
    private String uploadPath;

    @Value("${problem.upload.readme-path}")
    private String readmePath;

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
} 