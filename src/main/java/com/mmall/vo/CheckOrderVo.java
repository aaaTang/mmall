package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CheckOrderVo {

    private Integer id;

    private Long orderNo;

    private BigDecimal payment;

    private Integer startUser;

    private String startUserName;

    private Integer curUser;

    private String curUserName;

    private Integer curLv;

    private Integer status;

    private String statusDesc;

    private Integer lvId;

    private List<OrderItemVo> orderItemVoList;

    private String createTime;

}
