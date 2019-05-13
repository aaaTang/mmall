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
public class CheckOrder {
    private Integer id;

    private Long orderNo;

    private Integer startUser;

    private Integer curUser;

    private String checkOption;

    private Integer curLv;

    private Integer status;

    private Integer lvId;

    private Date createTime;

}