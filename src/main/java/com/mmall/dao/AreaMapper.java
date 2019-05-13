package com.mmall.dao;

import com.mmall.pojo.Area;

import java.util.List;

public interface AreaMapper {
    int deleteByPrimaryKey(Integer areaid);

    int insert(Area record);

    int insertSelective(Area record);

    Area selectByPrimaryKey(Integer areaid);

    List<Area> getProvinces();

    List<Area> getChildren(Integer id);

    int updateByPrimaryKeySelective(Area record);

    int updateByPrimaryKey(Area record);
}