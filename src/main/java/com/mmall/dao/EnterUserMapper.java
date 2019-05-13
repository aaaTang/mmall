package com.mmall.dao;

import com.mmall.pojo.EnterUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnterUserMapper {
    int deleteByPrimaryKey(Integer enterUserId);

    int insert(EnterUser record);

    int insertSelective(EnterUser record);

    EnterUser selectByPrimaryKey(Integer enterUserId);

    EnterUser selectByenterCoding(String enterCoding);

    int updateByPrimaryKeySelective(EnterUser record);

    int updateByPrimaryKey(EnterUser record);

    int checkUsername(String username);

    int checkEmail(String email);

    EnterUser selectLogin(@Param("username") String username, @Param("password") String password);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);

    List<Integer> selectAllUserId();

    List<EnterUser> selectAllEnterUser();

}