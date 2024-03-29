package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrder();

    List<Order> selectByYear(@Param("userId") Integer userId,@Param("tempStartTime") String tempStartTime,@Param("tempEndTime")String tempEndTime);

    List<Order> selectByUSerIdAndShippingid(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId,@Param("status") Integer status);

    List<Order> selectByStatus(Integer status);

    int selectNumByStatus(Integer status);

}