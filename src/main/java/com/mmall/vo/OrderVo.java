package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderVo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc; //payment的描述

    private Integer postage;  //运费

    private Integer expressType;

    private String expressTypeDesc;

    private String expressNub;

    private Integer curUserId;

    private String curUserName;

    private Integer status;

    private String statusDesc;  //状态的描述

    private CheckDetailVo checkDetailVo;//审核状态描述，若不是审核订单则为null；

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    //订单明细
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVo shippingVo;

    private EnterShippingVo enterShippingVo;

}
