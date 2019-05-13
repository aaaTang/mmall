package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class DzfpProduct {
    private Integer id;

    private String code;

    private String productName;

    private String jianma;

    private String shuimu;

    private String shuilv;

    private String xinghao;

    private String unit;

    private BigDecimal price;

    private String hsjbz;

    private String ycbz;

    private String zwhzyqt;

    private String ssflbm;

    private String yhzc;

    private String ssflbmName;

    private String yhzcType;

    private String lslbz;

    private String bmbbh;

}