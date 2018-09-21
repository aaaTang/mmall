package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentVo {

    private Integer commentId;

    private Integer userId;

    private String userName;

    private String headImg;

    private Integer starLevel;

    private Integer productId;

    private Integer typeId;

    private String typeName; //产品和型号名称组合

    private String content;

    private Boolean hasNewContent;

    private String newContent;

    private Date createTime;

    private Date updateTime;

}
