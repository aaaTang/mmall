package com.mmall.vo;

import com.mmall.pojo.HotProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotProductListVo {

    private List<HotProduct> hotProductList;

    private String imageHost;

}
