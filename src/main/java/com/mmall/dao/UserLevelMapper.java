package com.mmall.dao;

import com.mmall.pojo.UserLevel;

public interface UserLevelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLevel record);

    int insertSelective(UserLevel record);

    UserLevel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLevel record);

    int updateByPrimaryKey(UserLevel record);

    UserLevel selectBylv1(Integer id);

    UserLevel selectBylv2(Integer id);

    UserLevel selectBylv3(Integer id);

    UserLevel selectBylv4(Integer id);

}