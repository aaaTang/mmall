package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterShippingVo {

    private int enterShippingId;

    private int enterUserId;

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

    private boolean enterStatus;

    private String enterAfterWork;

}
