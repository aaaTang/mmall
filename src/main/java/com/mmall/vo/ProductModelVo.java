package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductModelVo {

    private Integer id;

    private String name;

    private BigDecimal price;

    private String unit;

    private Integer quantity;

    private String imageName;

    private boolean checked;

}
