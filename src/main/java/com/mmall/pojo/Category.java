package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Category {
    private Integer categoryId;

    private String categoryCode;

    private Integer jdCode;

    private Integer parentId;

    private String parentCode;

    private String categoryName;

    private Integer level;

    private Integer status;

    private Integer sortOrder;

    private BigDecimal discount;

    private Integer type;

    private Date createTime;

    private Date updateTime;

}