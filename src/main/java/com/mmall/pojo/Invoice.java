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

public class Invoice {
    private Integer fpId;

    private Integer userId;

    private String fpLsh;

    private Long orderNo;

    private String ewmUrl;

    private Date kprq;

    private String fpDm;

    private String fpHm;

    private String fpGf;

    private String fpGftax;

    private String fpXf;

    private String fpTax;

    private String pdfUrl;

    private String pdfBdurl;

    private BigDecimal hjbhsje;

    private BigDecimal kphjse;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}