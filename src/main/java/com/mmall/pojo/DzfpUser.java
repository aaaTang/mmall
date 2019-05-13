package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DzfpUser {
    private Integer id;

    private String code;

    private String name;

    private String jianma;

    private String tax;

    private String email;

}