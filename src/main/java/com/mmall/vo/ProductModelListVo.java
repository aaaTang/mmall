package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductModelListVo {

    private Integer id;

    private Integer productId;

    private String name;

    private BigDecimal price;

    private String unit;

    private Integer stock;

    private String imageName;

    private Integer status;

    private Integer orderby;

}
