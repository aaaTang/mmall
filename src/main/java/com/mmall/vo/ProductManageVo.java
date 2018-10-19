package com.mmall.vo;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductManageVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String mainImage;

    private BigDecimal sprice;

    private BigDecimal price;

    private Integer stock;

    private String brand;

    private Integer status;

}
