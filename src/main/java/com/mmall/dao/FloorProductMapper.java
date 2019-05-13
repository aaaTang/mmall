package com.mmall.dao;

import com.mmall.pojo.FloorProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FloorProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FloorProduct record);

    int insertSelective(FloorProduct record);

    FloorProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FloorProduct record);

    int updateByPrimaryKey(FloorProduct record);

    List<FloorProduct> selectFloorProduct(@Param("floorId") Integer floorId,@Param("categoryId") Integer categoryId);

    List<FloorProduct> selectAllFloorProduct();


}