package com.mmall.dao;

import com.mmall.pojo.SearchWords;

import java.util.List;

public interface SearchWordsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SearchWords record);

    int insertSelective(SearchWords record);

    SearchWords selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SearchWords record);

    int updateByPrimaryKey(SearchWords record);

    List<SearchWords> selectList();
}