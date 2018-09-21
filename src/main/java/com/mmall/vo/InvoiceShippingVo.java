package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceShippingVo {

    private Integer invoiceShippingId;

    private Integer userId;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;

    private String receiverName;

    private String receiverPhone;

    private String receiverTelephone;

    private Boolean shippingStatus;

}
