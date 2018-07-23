package com.mmall.vo;

import com.mmall.pojo.HotProduct;

import java.util.List;

public class HotProductListVo {

    private List<HotProduct> hotProductList;

    private String imageHost;

    public List<HotProduct> getHotProductList() {
        return hotProductList;
    }

    public void setHotProductList(List<HotProduct> hotProductList) {
        this.hotProductList = hotProductList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
