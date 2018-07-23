package com.mmall.vo;

import java.util.List;

public class ProductListTestVo {

    private String name;

    private String url;

    private int id;

    private int parentId;

    private List<ProductListVo> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<ProductListVo> getChildren() {
        return children;
    }

    public void setChildren(List<ProductListVo> children) {
        this.children = children;
    }
}
