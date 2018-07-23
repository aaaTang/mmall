package com.mmall.pojo;

public class SearchWords {
    private Integer id;

    private String name;

    private Integer orderStatus;

    public SearchWords(Integer id, String name, Integer orderStatus) {
        this.id = id;
        this.name = name;
        this.orderStatus = orderStatus;
    }

    public SearchWords() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
}