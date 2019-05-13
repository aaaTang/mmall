package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.vo.ProductPathVo;

import java.util.List;

public interface ICategoryService {

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

    ServerResponse<List<Category>> getList();

    ServerResponse<List<Category>> getEnterList();

    ServerResponse<ProductPathVo> getProductPath(int productId);

    ServerResponse saveCategory(Category category);

    ServerResponse deleteCategory(Integer categoryId);

}
