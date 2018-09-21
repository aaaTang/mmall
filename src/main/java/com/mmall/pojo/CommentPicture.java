package com.mmall.pojo;

import java.util.Date;

public class CommentPicture {
    private Integer commentPictureId;

    private Integer commentId;

    private String picUrl;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

    public CommentPicture(Integer commentPictureId, Integer commentId, String picUrl, Integer sortOrder, Date createTime, Date updateTime) {
        this.commentPictureId = commentPictureId;
        this.commentId = commentId;
        this.picUrl = picUrl;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public CommentPicture() {
        super();
    }

    public Integer getCommentPictureId() {
        return commentPictureId;
    }

    public void setCommentPictureId(Integer commentPictureId) {
        this.commentPictureId = commentPictureId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? null : picUrl.trim();
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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