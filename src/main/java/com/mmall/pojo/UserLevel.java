package com.mmall.pojo;

public class UserLevel {
    private Integer id;

    private Integer lv1;

    private Integer lv2;

    private Integer lv3;

    private Integer lv4;

    public UserLevel(Integer id, Integer lv1, Integer lv2, Integer lv3, Integer lv4) {
        this.id = id;
        this.lv1 = lv1;
        this.lv2 = lv2;
        this.lv3 = lv3;
        this.lv4 = lv4;
    }

    public UserLevel() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLv1() {
        return lv1;
    }

    public void setLv1(Integer lv1) {
        this.lv1 = lv1;
    }

    public Integer getLv2() {
        return lv2;
    }

    public void setLv2(Integer lv2) {
        this.lv2 = lv2;
    }

    public Integer getLv3() {
        return lv3;
    }

    public void setLv3(Integer lv3) {
        this.lv3 = lv3;
    }

    public Integer getLv4() {
        return lv4;
    }

    public void setLv4(Integer lv4) {
        this.lv4 = lv4;
    }
}