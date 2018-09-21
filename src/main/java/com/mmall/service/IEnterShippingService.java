package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterShipping;
import com.mmall.pojo.Shipping;

public interface IEnterShippingService {

    ServerResponse add(Integer userId, EnterShipping enterShipping, String staus);

    ServerResponse<String> delete(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,EnterShipping enterShipping,String status);

    ServerResponse<EnterShipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

    ServerResponse setDefault(Integer userId,int shippingId);

}
