package com.mmall.dao;

import com.mmall.pojo.HotWords;

import java.util.List;

public interface HotWordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HotWords record);

    int insertSelective(HotWords record);

    HotWords selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HotWords record);

    int updateByPrimaryKey(HotWords record);

    List<HotWords> selectList();
}