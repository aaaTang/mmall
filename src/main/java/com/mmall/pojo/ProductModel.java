package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Integer id;

    private Integer productId;

    private String name;

    private BigDecimal price;

    private String unit;

    private Integer stock;

    private String imageName;

    private Integer status;

    private Integer orderby;

    private Date createTime;

    private Date updateTime;

}