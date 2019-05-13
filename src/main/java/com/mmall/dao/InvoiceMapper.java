package com.mmall.dao;

import com.mmall.pojo.Invoice;

import java.util.List;

public interface InvoiceMapper {
    int deleteByPrimaryKey(Integer fpId);

    int insert(Invoice record);

    int insertSelective(Invoice record);

    Invoice selectByPrimaryKey(Integer fpId);

    int updateByPrimaryKeySelective(Invoice record);

    int updateByPrimaryKey(Invoice record);

    Invoice selectByOrderNo(Long orderNo);

    Invoice selectByLsh(String lsh);

    List<Invoice> selectByUserId(Integer userId);

    List<Invoice> selectByNoUserId();
}