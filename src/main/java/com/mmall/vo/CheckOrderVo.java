package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CheckOrderVo {

    private Integer id;

    private Long orderNo;

    private BigDecimal payment;

    private Integer startUser;

    private String startUserName;

    private Integer curUser;

    private String curUserName;

    private Integer curLv;

    private Integer status;

    private String statusDesc;

    private Integer lvId;

    private List<OrderItemVo> orderItemVoList;

    private String createTime;

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

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getStartUser() {
        return startUser;
    }

    public void setStartUser(Integer startUser) {
        this.startUser = startUser;
    }

    public String getStartUserName() {
        return startUserName;
    }

    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
    }

    public Integer getCurUser() {
        return curUser;
    }

    public void setCurUser(Integer curUser) {
        this.curUser = curUser;
    }

    public String getCurUserName() {
        return curUserName;
    }

    public void setCurUserName(String curUserName) {
        this.curUserName = curUserName;
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

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Integer getLvId() {
        return lvId;
    }

    public void setLvId(Integer lvId) {
        this.lvId = lvId;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
