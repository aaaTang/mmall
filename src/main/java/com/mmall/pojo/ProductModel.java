package com.mmall.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class ProductModel {
    private Integer id;

    private Integer productId;

    private String name;

    private BigDecimal price;

    private String unit;

    private Integer stock;

    private Integer status;

    private Integer orderby;

    private Date createTime;

    private Date updateTime;

    public ProductModel(Integer id, Integer productId, String name, BigDecimal price, String unit, Integer stock, Integer status, Integer orderby, Date createTime, Date updateTime) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.stock = stock;
        this.status = status;
        this.orderby = orderby;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public ProductModel() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderby() {
        return orderby;
    }

    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}