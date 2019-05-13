package com.mmall.dao;

import com.mmall.pojo.DzfpCart;

import java.util.List;

public interface DzfpCartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DzfpCart record);

    int insertSelective(DzfpCart record);

    DzfpCart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DzfpCart record);

    int updateByPrimaryKey(DzfpCart record);

    List<DzfpCart> selectAllDzfpCart();
}