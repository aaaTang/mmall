package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserVo {

    private Integer id;

    private String username;

    private String email;

    private String phone;

    private String question;

    private String answer;

    private Integer role;

    private String roleDesc;

    private Integer discount;

    private String headImg;

}
