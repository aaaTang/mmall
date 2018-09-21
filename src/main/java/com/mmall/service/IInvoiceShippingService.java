package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.InvoiceShipping;
import com.mmall.vo.InvoiceShippingVo;

public interface IInvoiceShippingService {

    ServerResponse add(Integer userId, InvoiceShipping invoiceShipping, String staus);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,InvoiceShipping invoiceShipping,String status);

    ServerResponse<InvoiceShippingVo> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

    ServerResponse setDefault(Integer userId,int shippingId);

}
