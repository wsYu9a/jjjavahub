package com.wsyu9a.service.impl;

import com.wsyu9a.dto.CategoryDTO;
import com.wsyu9a.entity.Category;
import com.wsyu9a.exception.BusinessException;
import com.wsyu9a.mapper.CategoryMapper;
import com.wsyu9a.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        try {
            return categoryMapper.findAll();
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            throw new BusinessException("获取分类列表失败");
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        try {
            Category category = categoryMapper.findById(id);
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
            return category;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            throw new BusinessException("获取分类详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category addCategory(CategoryDTO categoryDTO) {
        try {
            // 检查分类名称是否已存在
            List<Category> existingCategories = categoryMapper.findAll();
            boolean exists = existingCategories.stream()
                    .anyMatch(c -> c.getName().equals(categoryDTO.getName()));
            if (exists) {
                throw new BusinessException("分类名称已存在");
            }

            // 创建新分类
            Category category = new Category();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());

            // 保存分类
            int rows = categoryMapper.insert(category);
            if (rows != 1) {
                throw new BusinessException("添加分类失败");
            }

            log.info("分类添加成功: {}", category.getName());
            return category;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加分类失败", e);
            throw new BusinessException("添加分类失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(CategoryDTO categoryDTO) {
        try {
            // 检查分类是否存在
            Category category = categoryMapper.findById(categoryDTO.getId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }

            // 检查新名称是否与其他分类重复
            List<Category> existingCategories = categoryMapper.findAll();
            boolean exists = existingCategories.stream()
                    .anyMatch(c -> c.getName().equals(categoryDTO.getName()) 
                            && !c.getId().equals(categoryDTO.getId()));
            if (exists) {
                throw new BusinessException("分类名称已存在");
            }

            // 更新分类信息
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            category.setUpdateTime(LocalDateTime.now());

            // 保存更新
            int rows = categoryMapper.update(category);
            if (rows != 1) {
                throw new BusinessException("更新分类失败");
            }

            log.info("分类更新成功: {}", category.getName());
            return category;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新分类失败", e);
            throw new BusinessException("更新分类失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        try {
            // 检查分类是否存在
            Category category = categoryMapper.findById(id);
            if (category == null) {
                throw new BusinessException("分类不存在");
            }

            // 检查分类下是否有题目
            int count = categoryMapper.countProblemsByCategory(id);
            if (count > 0) {
                throw new BusinessException("该分类下还有题目，无法删除");
            }

            // 删除分类
            int rows = categoryMapper.delete(id);
            if (rows != 1) {
                throw new BusinessException("删除分类失败");
            }

            log.info("分类删除成功: {}", category.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除分类失败", e);
            throw new BusinessException("删除分类失败，请稍后重试");
        }
    }
} 