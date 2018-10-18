package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OrderItemVo {

    private Integer orderItemId;

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Boolean modelStatus;

    private String modelName;

    private BigDecimal modelPrice;

    private String modelUnit;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;

}
