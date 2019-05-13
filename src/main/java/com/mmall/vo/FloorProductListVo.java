package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class FloorProductListVo {

    private Integer floorProductId;

    private Integer id;

    private Integer categoryId;

    private String categoryName;

    private String name;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

}
