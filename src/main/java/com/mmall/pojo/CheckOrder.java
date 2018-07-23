package com.mmall.pojo;

import java.util.Date;

public class CheckOrder {
    private Integer id;

    private Long orderNo;

    private Integer startUser;

    private Integer curUser;

    private Integer curLv;

    private Integer status;

    private Integer lvId;

    private Date createTime;

    public CheckOrder(Integer id, Long orderNo, Integer startUser, Integer curUser, Integer curLv, Integer status, Integer lvId,Date createTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.startUser = startUser;
        this.curUser = curUser;
        this.curLv = curLv;
        this.status = status;
        this.lvId = lvId;
        this.createTime=createTime;
    }

    public CheckOrder() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStartUser() {
        return startUser;
    }

    public void setStartUser(Integer startUser) {
        this.startUser = startUser;
    }

    public Integer getCurUser() {
        return curUser;
    }

    public void setCurUser(Integer curUser) {
        this.curUser = curUser;
    }

    public Integer getCurLv() {
        return curLv;
    }

    public void setCurLv(Integer curLv) {
        this.curLv = curLv;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLvId() {
        return lvId;
    }

    public void setLvId(Integer lvId) {
        this.lvId = lvId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}