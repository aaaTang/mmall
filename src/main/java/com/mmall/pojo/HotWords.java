package com.mmall.pojo;

import java.util.Date;

public class HotWords {
    private Integer id;

    private String word;

    private Integer categoryId;

    private Boolean status;

    private Date createTime;

    private Date updateTime;

    public HotWords(Integer id, String word, Integer categoryId, Boolean status, Date createTime, Date updateTime) {
        this.id = id;
        this.word = word;
        this.categoryId = categoryId;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public HotWords() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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