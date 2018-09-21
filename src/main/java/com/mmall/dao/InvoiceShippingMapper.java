package com.mmall.dao;

import com.mmall.pojo.InvoiceShipping;
import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InvoiceShippingMapper {
    int deleteByPrimaryKey(Integer invoiceShippingId);

    int insert(InvoiceShipping record);

    int insertSelective(InvoiceShipping record);

    InvoiceShipping selectByPrimaryKey(Integer invoiceShippingId);

    int updateByPrimaryKeySelective(InvoiceShipping record);

    int updateByPrimaryKey(InvoiceShipping record);

    int deleteByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByShipping(InvoiceShipping record);

    InvoiceShipping selectByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<InvoiceShipping> selectByUserId(@Param("userId") Integer userId);

    int updateStatus(InvoiceShipping record);

    InvoiceShipping selectDefault(Integer userId);

    int selectMaxId();
}