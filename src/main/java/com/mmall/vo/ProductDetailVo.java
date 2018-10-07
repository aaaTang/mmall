package com.mmall.vo;

import com.mmall.pojo.ProductModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductDetailVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String[] smallImages;

    private String[] bigImages;

    private String[] subImages;

    private String detail;

    private BigDecimal sprice;

    private BigDecimal price;

    private Integer stock;

    private String brand;

    private String weight;

    private String originCountry;

    private String itemDetail;

    private Integer status;

    private String createTime;

    private String updateTime;

    private List<ProductModel> productModelList;

}
