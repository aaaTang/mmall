package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Drawback {
    private Integer id;

    private Date orderTime;

    private Integer userId;

    private Long orderNo;

    private Integer serviceType;

    private BigDecimal drawbackMoney;

    private Integer reason;

    private Integer refundWay;

    private String description;

    private Date createTime;

    private Date updateTime;

}