package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EnterShipping {
    private Integer enterShippingId;

    private Integer enterUserId;

    private String enterReceiverName;

    private String enterReceiverTelephone;

    private String enterReceiverPhone;

    private String enterReceiverProvince;

    private String enterReceiverCity;

    private String enterReceiverDistrict;

    private String enterReceiverAddress;

    private Integer enterReceiverFloor;

    private String enterReceiverTime;

    private Integer enterTruck;

    private Integer enterStatus;

    private String enterAfterWork;

    private Date createTime;

    private Date updateTime;

}