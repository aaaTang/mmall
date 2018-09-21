package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceShipping {
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

    private Integer shippingStatus;

    private Date createTime;

    private Date updateTime;

}