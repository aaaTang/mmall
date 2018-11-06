package com.mmall.common;

public enum CategoryCode {

    NULL(0,"编码为空"),
    EMPTY(1,"产品为空"),
    FULL(2,"拥有产品");

    private final int code;
    private final String desc;

    CategoryCode(int code, String desc){
        this.code=code;
        this.desc=desc;
    }

    public int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
