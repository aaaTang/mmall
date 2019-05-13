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

public class LoginRecord {
    private Integer id;

    private String username;

    private String password;

    private Integer type;

    private Date time;

    private Integer issuccess;

}