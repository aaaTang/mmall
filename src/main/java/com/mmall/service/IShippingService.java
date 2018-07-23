package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping,String staus);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,Shipping shipping,String status);

    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

    ServerResponse setDefault(Integer userId,int shippingId);

}
