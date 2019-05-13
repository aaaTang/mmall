package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class DzfpItem {
    private Integer id;

    private String fpLsh;

    private String fpFlh;

    private String productName;

    private String unit;

    private String xinghao;

    private Float count;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private BigDecimal sl;

    private BigDecimal se;

}