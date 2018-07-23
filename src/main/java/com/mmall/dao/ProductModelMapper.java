package com.mmall.dao;

import com.mmall.pojo.ProductModel;

import java.util.List;

public interface ProductModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductModel record);

    int insertSelective(ProductModel record);

    ProductModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductModel record);

    int updateByPrimaryKey(ProductModel record);

    List<ProductModel> selectModelList(Integer productId);
}