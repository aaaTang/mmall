package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartProductVo {

    //综合了产品和购物车的一个抽象对象

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private boolean productChecked; //此商品是否勾选；
    private String limitQuantity; //限制数量的一个返回结果；

    private boolean modelExist;  //判断商品型号是否存在，如果存在就返回一个True，不存在就返回False；
    private List<ProductModelVo> productModelVoList;  //商品型号VO

}
