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
public class HotWords {
    private Integer id;

    private String word;

    private Integer categoryId;

    private Boolean status;

    private Date createTime;

    private Date updateTime;


}