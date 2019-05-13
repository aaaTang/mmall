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

public class EnterUser {
    private Integer enterUserId;

    private String enterUserPassword;

    private String enterCoding;

    private String enterName;

    private String telephone;

    private String emerTelephone;

    private Integer discount;

    private String headImg;

    private Integer balance;

    private String phone;

    private String fax;

    private String qq;

    private String leperson;

    private String texType;

    private String invoiceBank;

    private String invoiceCount;

    private String invoiceNumber;

    private String invoiceAddress;

    private String invoicePhone;

    private Date createTime;

    private Date updateTime;

}