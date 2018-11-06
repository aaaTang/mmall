package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryProduct {

    private Integer categoryId;

    private String categoryName;

    private List<ProductNumVo> productNumVoList;

}
