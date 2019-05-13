package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

public class FloorVo {

    private Integer id;

    private String floorName;

    private String floorImage;

    private String firstCategoryname;

    private Integer firstCategoryid;

    private String secondCategoryname;

    private Integer secondCategoryid;

    private String thirdCategoryname;

    private Integer thirdCategoryid;

    private String moreCategoryname;

    private Integer moreCategoryid;

    private Integer sortOrder;

    private List<FloorProductListVo> firstProductList;

    private List<FloorProductListVo> secondProductList;

    private List<FloorProductListVo> thirdProductList;

}
