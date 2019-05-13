package com.mmall.dao;

import com.mmall.pojo.DzfpProduct;

import java.util.List;

public interface DzfpProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DzfpProduct record);

    int insertSelective(DzfpProduct record);

    DzfpProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DzfpProduct record);

    int updateByPrimaryKey(DzfpProduct record);

    List<DzfpProduct> selectByProductName(String productName);
}