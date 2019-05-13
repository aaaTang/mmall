package com.mmall.dao;

import com.mmall.pojo.TokenUser;
import org.apache.ibatis.annotations.Param;

public interface TokenUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TokenUser record);

    int insertSelective(TokenUser record);

    TokenUser selectByPrimaryKey(Integer id);

    TokenUser selectByUserNameAndPassword(@Param("username")String username, @Param("password")String password);

    int updateByPrimaryKeySelective(TokenUser record);

    int updateByPrimaryKey(TokenUser record);
}