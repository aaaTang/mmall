package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryVo {

    private Integer categoryId;

    private String categoryName;

    private Integer status;

    private String statusDesc;

    private Integer number;
}
