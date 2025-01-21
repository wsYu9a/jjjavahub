package com.wsyu9a.controller.admin;

import com.wsyu9a.common.Result;
import com.wsyu9a.dto.CategoryDTO;
import com.wsyu9a.entity.Category;
import com.wsyu9a.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            log.info("获取分类列表成功: {}", categories);
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return Result.success("获取成功", category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Category> addCategory(@Validated @RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = categoryService.addCategory(categoryDTO);
            return Result.success("添加成功", category);
        } catch (Exception e) {
            log.error("添加分类失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Category> updateCategory(@Validated @RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = categoryService.updateCategory(categoryDTO);
            return Result.success("更新成功", category);
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.fail(e.getMessage());
        }
    }
} 