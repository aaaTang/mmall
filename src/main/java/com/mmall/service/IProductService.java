package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.ProductModel;
import com.mmall.vo.*;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);

    ServerResponse<PageInfoAndBrandVo> getProductListTest(int pageNum,int pageSize,int categoryId);

    ServerResponse<List<ProductSugVo>> getProductSugList(int categoryId);

    ServerResponse<List<ProductSugVo>> getProductLoveList();

    ServerResponse addCollect(Integer userId,Integer productId);

    ServerResponse delectCollect(Integer userId,Integer productId);

    ServerResponse<PageInfo> getCollect(Integer role,Integer userId,int pageNum,int pageSize);

    ServerResponse<PageInfoAndBrandVo> getProductListByKeyword(int pageNum,int pageSize,String keyword);

    ServerResponse<PageInfo> getProduct(Integer zb,Integer categoryId,int pageNum,int pageSize);

    ServerResponse updateProduct(Integer id, String name, BigDecimal sprice, BigDecimal price, String brand, Integer status);

    ServerResponse changeCategory(Integer productId,Integer categoryId);

    ServerResponse<List<testVo>> getAllProduct();

    ServerResponse<PageInfo> getProductByCategoryIdAndBrand(int pageNum,int pageSize,int categoryId,String brand);

    ServerResponse<PageInfo> getProductByKeywordIdAndBrand(int pageNum,int pageSize,String keyword,String brand);

    ServerResponse selectNull();

    ServerResponse getProductModel(Integer prdouctId,Integer pageNum,Integer pageSize);

    ServerResponse saveProductModel(ProductModel productModel);

    ServerResponse deleteProductModel(Integer productModelId);

}
