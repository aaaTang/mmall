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

public class FloorCategory {
    private Integer id;

    private String floorName;

    private String floorImage;

    private Integer firstCategoryid;

    private Integer secondCategoryid;

    private Integer thirdCategoryid;

    private Integer moreCategoryid;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;
}