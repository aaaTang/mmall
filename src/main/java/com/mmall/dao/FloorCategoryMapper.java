package com.mmall.dao;

import com.mmall.pojo.FloorCategory;

import java.util.List;

public interface FloorCategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FloorCategory record);

    int insertSelective(FloorCategory record);

    FloorCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FloorCategory record);

    int updateByPrimaryKey(FloorCategory record);

    List<FloorCategory> selectFloor();

    List<FloorCategory> selectFloorSort();
}