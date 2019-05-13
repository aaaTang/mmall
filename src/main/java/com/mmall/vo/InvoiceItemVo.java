package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class InvoiceItemVo {

    private String productName;

    private String unit;

    private String xinghao;

    private Float count;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private BigDecimal sl;

    private BigDecimal se;

}
