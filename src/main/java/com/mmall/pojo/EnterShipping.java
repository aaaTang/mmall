package com.mmall.pojo;

import java.util.Date;

public class EnterShipping {
    private Integer enterShippingId;

    private Integer enterUserId;

    private String enterReceiverName;

    private String enterReceiverTelephone;

    private String enterReceiverPhone;

    private String enterReceiverProvince;

    private String enterReceiverCity;

    private String enterReceiverDistrict;

    private String enterReceiverAddress;

    private Integer enterReceiverFloor;

    private String enterReceiverTime;

    private Integer enterTruck;

    private Integer enterStatus;

    private String enterAfterWork;

    private Date createTime;

    private Date updateTime;

    public EnterShipping(Integer enterShippingId, Integer enterUserId, String enterReceiverName, String enterReceiverTelephone, String enterReceiverPhone, String enterReceiverProvince, String enterReceiverCity, String enterReceiverDistrict, String enterReceiverAddress, Integer enterReceiverFloor, String enterReceiverTime, Integer enterTruck, Integer enterStatus, String enterAfterWork, Date createTime, Date updateTime) {
        this.enterShippingId = enterShippingId;
        this.enterUserId = enterUserId;
        this.enterReceiverName = enterReceiverName;
        this.enterReceiverTelephone = enterReceiverTelephone;
        this.enterReceiverPhone = enterReceiverPhone;
        this.enterReceiverProvince = enterReceiverProvince;
        this.enterReceiverCity = enterReceiverCity;
        this.enterReceiverDistrict = enterReceiverDistrict;
        this.enterReceiverAddress = enterReceiverAddress;
        this.enterReceiverFloor = enterReceiverFloor;
        this.enterReceiverTime = enterReceiverTime;
        this.enterTruck = enterTruck;
        this.enterStatus = enterStatus;
        this.enterAfterWork = enterAfterWork;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public EnterShipping() {
        super();
    }

    public Integer getEnterShippingId() {
        return enterShippingId;
    }

    public void setEnterShippingId(Integer enterShippingId) {
        this.enterShippingId = enterShippingId;
    }

    public Integer getEnterUserId() {
        return enterUserId;
    }

    public void setEnterUserId(Integer enterUserId) {
        this.enterUserId = enterUserId;
    }

    public String getEnterReceiverName() {
        return enterReceiverName;
    }

    public void setEnterReceiverName(String enterReceiverName) {
        this.enterReceiverName = enterReceiverName == null ? null : enterReceiverName.trim();
    }

    public String getEnterReceiverTelephone() {
        return enterReceiverTelephone;
    }

    public void setEnterReceiverTelephone(String enterReceiverTelephone) {
        this.enterReceiverTelephone = enterReceiverTelephone == null ? null : enterReceiverTelephone.trim();
    }

    public String getEnterReceiverPhone() {
        return enterReceiverPhone;
    }

    public void setEnterReceiverPhone(String enterReceiverPhone) {
        this.enterReceiverPhone = enterReceiverPhone == null ? null : enterReceiverPhone.trim();
    }

    public String getEnterReceiverProvince() {
        return enterReceiverProvince;
    }

    public void setEnterReceiverProvince(String enterReceiverProvince) {
        this.enterReceiverProvince = enterReceiverProvince == null ? null : enterReceiverProvince.trim();
    }

    public String getEnterReceiverCity() {
        return enterReceiverCity;
    }

    public void setEnterReceiverCity(String enterReceiverCity) {
        this.enterReceiverCity = enterReceiverCity == null ? null : enterReceiverCity.trim();
    }

    public String getEnterReceiverDistrict() {
        return enterReceiverDistrict;
    }

    public void setEnterReceiverDistrict(String enterReceiverDistrict) {
        this.enterReceiverDistrict = enterReceiverDistrict == null ? null : enterReceiverDistrict.trim();
    }

    public String getEnterReceiverAddress() {
        return enterReceiverAddress;
    }

    public void setEnterReceiverAddress(String enterReceiverAddress) {
        this.enterReceiverAddress = enterReceiverAddress == null ? null : enterReceiverAddress.trim();
    }

    public Integer getEnterReceiverFloor() {
        return enterReceiverFloor;
    }

    public void setEnterReceiverFloor(Integer enterReceiverFloor) {
        this.enterReceiverFloor = enterReceiverFloor;
    }

    public String getEnterReceiverTime() {
        return enterReceiverTime;
    }

    public void setEnterReceiverTime(String enterReceiverTime) {
        this.enterReceiverTime = enterReceiverTime == null ? null : enterReceiverTime.trim();
    }

    public Integer getEnterTruck() {
        return enterTruck;
    }

    public void setEnterTruck(Integer enterTruck) {
        this.enterTruck = enterTruck;
    }

    public Integer getEnterStatus() {
        return enterStatus;
    }

    public void setEnterStatus(Integer enterStatus) {
        this.enterStatus = enterStatus;
    }

    public String getEnterAfterWork() {
        return enterAfterWork;
    }

    public void setEnterAfterWork(String enterAfterWork) {
        this.enterAfterWork = enterAfterWork == null ? null : enterAfterWork.trim();
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