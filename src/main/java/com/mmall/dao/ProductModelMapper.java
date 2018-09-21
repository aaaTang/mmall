package com.mmall.dao;

import com.mmall.pojo.ProductModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductModel record);

    int insertSelective(ProductModel record);

    ProductModel selectByPrimaryKey(Integer id);

    ProductModel selectByIdProductId(@Param("id") Integer id,@Param("productId") Integer productId);

    List<ProductModel> selectByProductId(Integer productId);

    int updateByPrimaryKeySelective(ProductModel record);

    int updateByPrimaryKey(ProductModel record);

    List<ProductModel> selectModelList(Integer productId);

}