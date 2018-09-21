package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingVo {

    private Integer id;

    private Integer userId;

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;

    private boolean status;

}
