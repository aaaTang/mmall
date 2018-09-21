package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MessageVo {

    private Integer id;

    private String title;

    private String content;

    private String messageUrl;

    private String operator;

    private Integer read;

    private Date createTime;

}
