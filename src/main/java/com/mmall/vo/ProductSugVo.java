package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSugVo {

    private Integer productId;

    private String image;

    private String name;

    private BigDecimal price;

}
