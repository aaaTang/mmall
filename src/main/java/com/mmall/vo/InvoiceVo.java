package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter

public class InvoiceVo {

    private String fpLsh;

    private String kprq;

    private String fpDm;

    private String fpHm;

    private String fpGf;

    private String fpGftax;

    private String fpXf;

    private String fpTax;

    private String pdfBdurl;

    private BigDecimal hjbhsje;

    private BigDecimal kphjse;

    private Integer status;

    private String statusDesc;

    private List<InvoiceItemVo> invoiceItemVoList;

}
