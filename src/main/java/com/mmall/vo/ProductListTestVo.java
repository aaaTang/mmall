package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductListTestVo {

    private String name;

    private String url;

    private int id;

    private int parentId;

    private List<ProductListVo> children;

}
