package com.mmall.pojo;

import java.util.Date;

public class EnterUser {
    private Integer enterUserId;

    private String enterUserPassword;

    private String enterCoding;

    private String enterName;

    private String telephone;

    private String emerTelephone;

    private Integer discount;

    private String headImg;

    private Integer balance;

    private String phone;

    private String fax;

    private String qq;

    private String leperson;

    private String texType;

    private String invoiceBank;

    private String invoiceCount;

    private String invoiceNumber;

    private String invoiceAddress;

    private String invoicePhone;

    private Date createTime;

    private Date updateTime;

    public EnterUser(Integer enterUserId, String enterUserPassword, String enterCoding, String enterName, String telephone, String emerTelephone, Integer discount, String headImg, Integer balance, String phone, String fax, String qq, String leperson, String texType, String invoiceBank, String invoiceCount, String invoiceNumber, String invoiceAddress, String invoicePhone, Date createTime, Date updateTime) {
        this.enterUserId = enterUserId;
        this.enterUserPassword = enterUserPassword;
        this.enterCoding = enterCoding;
        this.enterName = enterName;
        this.telephone = telephone;
        this.emerTelephone = emerTelephone;
        this.discount = discount;
        this.headImg = headImg;
        this.balance = balance;
        this.phone = phone;
        this.fax = fax;
        this.qq = qq;
        this.leperson = leperson;
        this.texType = texType;
        this.invoiceBank = invoiceBank;
        this.invoiceCount = invoiceCount;
        this.invoiceNumber = invoiceNumber;
        this.invoiceAddress = invoiceAddress;
        this.invoicePhone = invoicePhone;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public EnterUser() {
        super();
    }

    public Integer getEnterUserId() {
        return enterUserId;
    }

    public void setEnterUserId(Integer enterUserId) {
        this.enterUserId = enterUserId;
    }

    public String getEnterUserPassword() {
        return enterUserPassword;
    }

    public void setEnterUserPassword(String enterUserPassword) {
        this.enterUserPassword = enterUserPassword == null ? null : enterUserPassword.trim();
    }

    public String getEnterCoding() {
        return enterCoding;
    }

    public void setEnterCoding(String enterCoding) {
        this.enterCoding = enterCoding == null ? null : enterCoding.trim();
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName == null ? null : enterName.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmerTelephone() {
        return emerTelephone;
    }

    public void setEmerTelephone(String emerTelephone) {
        this.emerTelephone = emerTelephone == null ? null : emerTelephone.trim();
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg == null ? null : headImg.trim();
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getLeperson() {
        return leperson;
    }

    public void setLeperson(String leperson) {
        this.leperson = leperson == null ? null : leperson.trim();
    }

    public String getTexType() {
        return texType;
    }

    public void setTexType(String texType) {
        this.texType = texType == null ? null : texType.trim();
    }

    public String getInvoiceBank() {
        return invoiceBank;
    }

    public void setInvoiceBank(String invoiceBank) {
        this.invoiceBank = invoiceBank == null ? null : invoiceBank.trim();
    }

    public String getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(String invoiceCount) {
        this.invoiceCount = invoiceCount == null ? null : invoiceCount.trim();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber == null ? null : invoiceNumber.trim();
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress == null ? null : invoiceAddress.trim();
    }

    public String getInvoicePhone() {
        return invoicePhone;
    }

    public void setInvoicePhone(String invoicePhone) {
        this.invoicePhone = invoicePhone == null ? null : invoicePhone.trim();
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