package com.mmall.dao;

import com.mmall.pojo.HotProduct;

import java.util.List;

public interface HotProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HotProduct record);

    int insertSelective(HotProduct record);

    HotProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HotProduct record);

    int updateByPrimaryKey(HotProduct record);

    List<HotProduct> getList();
}