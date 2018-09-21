package com.mmall.vo;

import com.mmall.pojo.ProductModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductDetailVo {

    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String[] imageArray;
    private String[] detail;
    private BigDecimal sprice;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private String createTime;
    private String updateTime;
    private String imageHost;
    private Integer parentCategoryId;
    private List<ProductModel> productModelList;

}