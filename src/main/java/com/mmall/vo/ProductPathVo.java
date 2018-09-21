package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPathVo {

    private Integer productId;

    private String productName;

    private Integer firstCategoryid;

    private String firstCategoryName;

    private Integer secondCategoryId;

    private String secondCategoryName;

    private Integer thirdCategoryId;

    private String thirdCategoryName;

}
