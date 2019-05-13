package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DrawbackVo {

    private Integer id;

    private String orderTime;

    private Integer userId;

    private Long orderNo;

    private Integer serviceType;

    private String serviceTypeDesc;

    private BigDecimal drawbackMoney;

    private Integer reason;

    private String reasonDesc;

    private Integer refundWay;

    private String refundWayDesc;

    private String description;

    private OrderVo orderVo;

}
