package com.mmall.dao;

import com.mmall.pojo.CheckOrder;

import java.util.List;

public interface CheckOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CheckOrder record);

    int insertSelective(CheckOrder record);

    CheckOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CheckOrder record);

    int updateByPrimaryKey(CheckOrder record);

    List<CheckOrder> selectCheckOrderList(Integer curUser);

    List<CheckOrder> selectCheckOrderList4(Integer curUser);

    List<CheckOrder> selectCheckOrderListByStartUser(Integer startUser);

    CheckOrder selectByOrderNo(Long orderNo);
}