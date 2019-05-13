package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CheckDetailVo {

    private Integer currentLv; //当前审核员级别

    private String currentName; //当前审核员姓名；

    private String checkOption; //审核意见；

}
