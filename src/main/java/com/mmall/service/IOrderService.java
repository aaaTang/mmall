package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse setPay(Long orderNo, Integer userId);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse submit(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getCheckList(Integer userId,int pageNum, int pageSize);

    ServerResponse checkOrder(int userId,int checkOrderId,int lvId,int status);

    ServerResponse confirmOrder(Integer userId, Long orderNo);

    ServerResponse delete(Integer userId, Long orderNo);

    //backend
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
