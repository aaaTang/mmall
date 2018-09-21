package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer messageInfoId;

    private String title;

    private String content;

    private String messageUrl;

    private String operator;

    private Integer goalUser;

    private Integer read;

    private Date createTime;

}