package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class FloorProductVo {

    private Integer id;

    private Integer floorId;

    private String floorName;

    private Integer productId;

    private Integer categoryId;

    private String categoryName;

    private String name;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

}
