package com.wsyu9a.mapper;

import com.wsyu9a.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    
    @Select("SELECT * FROM problem_category")
    List<Category> findAll();

    @Select("SELECT * FROM problem_category WHERE id = #{id}")
    Category findById(Long id);
    
    @Insert("INSERT INTO problem_category(name, description, create_time, update_time) " +
            "VALUES(#{name}, #{description}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    @Update("UPDATE problem_category SET name = #{name}, description = #{description}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int update(Category category);
    
    @Delete("DELETE FROM problem_category WHERE id = #{id}")
    int delete(Long id);
    
    @Select("SELECT COUNT(*) FROM problem WHERE category_id = #{categoryId}")
    int countProblemsByCategory(Long categoryId);
} 