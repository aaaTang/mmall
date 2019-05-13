package com.mmall.dao;

import com.mmall.pojo.Skus;

import java.util.List;

public interface SkusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Skus record);

    int insertSelective(Skus record);

    Skus selectByPrimaryKey(Integer id);

    List<Skus> selectByCategoryId(Integer categoryId);

    int updateByPrimaryKeySelective(Skus record);

    int updateByPrimaryKey(Skus record);
}