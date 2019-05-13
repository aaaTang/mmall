package com.mmall.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageInfoAndBrandVo {

    private PageInfo pageInfo;

    private List<String> brandList;

}
