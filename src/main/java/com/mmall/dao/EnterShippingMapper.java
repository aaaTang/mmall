package com.mmall.dao;

import com.mmall.pojo.EnterShipping;
import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnterShippingMapper {
    int deleteByPrimaryKey(Integer enterShippingId);

    int insert(EnterShipping record);

    int insertSelective(EnterShipping record);

    EnterShipping selectByPrimaryKey(Integer enterShippingId);

    int updateByPrimaryKeySelective(EnterShipping record);

    int updateByPrimaryKey(EnterShipping record);

    EnterShipping selectDefault(Integer userId);

    int updateStatus(EnterShipping record);

    int deleteByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByShipping(EnterShipping record);

    EnterShipping selectByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<EnterShipping> selectByUserId(@Param("userId") Integer userId);

    int selectMaxId();
}