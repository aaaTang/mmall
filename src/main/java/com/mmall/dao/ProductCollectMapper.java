package com.mmall.dao;

import com.mmall.pojo.ProductCollect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCollectMapper {
    int deleteByPrimaryKey(Integer productCollectId);

    int insert(ProductCollect record);

    int insertSelective(ProductCollect record);

    ProductCollect selectByPrimaryKey(Integer productCollectId);

    int updateByPrimaryKeySelective(ProductCollect record);

    int updateByPrimaryKey(ProductCollect record);

    List<ProductCollect> selectByUserId(int userId);

    int deleteByUserIdProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    ProductCollect selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);
}