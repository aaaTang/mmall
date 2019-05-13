package com.mmall.pojo;

public class Skus {
    private Integer id;

    private Integer sku;

    private Integer categoryId;

    private Integer status;

    public Skus(Integer id, Integer sku, Integer categoryId, Integer status) {
        this.id = id;
        this.sku = sku;
        this.categoryId = categoryId;
        this.status = status;
    }

    public Skus() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}