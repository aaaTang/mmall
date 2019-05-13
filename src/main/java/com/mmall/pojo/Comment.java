package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Comment {
    private Integer commentId;

    private Integer userId;

    private Integer productId;

    private Integer typeId;

    private Long orderNo;

    private Integer orderitemId;

    private Integer status;

    private Integer starLevel;

    private String content;

    private String newContent;

    private Date createTime;

    private Date updateTime;

}