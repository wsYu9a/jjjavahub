package com.wsyu9a.service;

import com.wsyu9a.dto.CategoryDTO;
import com.wsyu9a.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category addCategory(CategoryDTO categoryDTO);
    Category updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(Long id);
} 