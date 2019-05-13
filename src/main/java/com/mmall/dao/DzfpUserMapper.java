package com.mmall.dao;

import com.mmall.pojo.DzfpUser;

public interface DzfpUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DzfpUser record);

    int insertSelective(DzfpUser record);

    DzfpUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DzfpUser record);

    int updateByPrimaryKey(DzfpUser record);
}