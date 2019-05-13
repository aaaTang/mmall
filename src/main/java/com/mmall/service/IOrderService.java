package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderNumVo;
import com.mmall.vo.OrderVo;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId);

    ServerResponse setPay(Long orderNo, Integer userId);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse quickCreateOrder(Integer userId,Integer productId,Integer modelId,Integer count,Integer shippingId);

    ServerResponse<String> cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse submit(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getCheckList(Integer userId,int pageNum, int pageSize);

    ServerResponse fourCheckOrder(int userId,int checkOrderId,int lvId,int status,String checkOption);

    ServerResponse threeCheckOrder(int userId,int checkOrderId,int lvId,int status);

    ServerResponse confirmOrder(Integer userId, Long orderNo);

    ServerResponse delete(Integer userId, Long orderNo);

    ServerResponse<List<OrderVo>> selectOrder(int userId, int year, int month, String startTime, String endTime);

    //backend
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Integer status,String name,Long orderNo,String phone,int pageNum,int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo,Integer type,String num);

    ServerResponse<String> query(String orderNo);

    ServerResponse drawback(int userId, Long orderNo, int type, BigDecimal money, int reason, int refund_way, String description);

    ServerResponse<OrderNumVo> getOrderNum();

    void setPayStatus(String orderNo);

    ServerResponse<PageInfo> getDrawback(Integer pageNum,Integer pageSize);

    ServerResponse changePayment(Long orderNo, BigDecimal payment);

    ServerResponse changeOrderPrice(Long orderNo,Integer productId,Integer productModelId, BigDecimal payment);

    ServerResponse kjfp(Long orderNo, String companyName,String tax);

    ServerResponse sendMail(String mail,Long orderNo);

    ServerResponse fpList(Integer userId,Integer pageNum,Integer pageSize);

}
