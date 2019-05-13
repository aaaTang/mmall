package com.mmall.dao;

import com.mmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    Category selectByCategoryCode(String categoryCode);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    List<Category> getList();

    List<Category> getAllList();

    List<Category> getAllSecondCategory();

    List<Category> getSecondCategoryHas();

    List<Category> getApiCategory();

    Category selectByJdCode(int jdCode);
}