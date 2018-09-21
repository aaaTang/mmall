package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.createOrder(user.getId(),shippingId);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.createOrder(enterUser.getEnterUserId(),shippingId);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("quick_create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer productId,@RequestParam(value = "modelId",defaultValue = "0")Integer modelId,Integer count,Integer shippingId){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.quickCreateOrder(user.getId(),productId,modelId,count,shippingId);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.quickCreateOrder(enterUser.getEnterUserId(),productId,modelId,count,shippingId);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.cancel(user.getId(),orderNo);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.cancel(enterUser.getEnterUserId(),orderNo);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.delete(user.getId(),orderNo);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.delete(enterUser.getEnterUserId(),orderNo);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderCartProduct(user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderCartProduct(enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderDetail(user.getId(),orderNo);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderDetail(enterUser.getEnterUserId(),orderNo);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getOrderList(enterUser.getEnterUserId(),pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("submit.do")
    @ResponseBody
    public ServerResponse submit(HttpSession session, Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.submit(user.getId(), orderNo);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.submit(enterUser.getEnterUserId(), orderNo);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("get_check.do")
    @ResponseBody
    public ServerResponse getCheckList(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getCheckList(user.getId(),pageNum,pageSize);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.getCheckList(enterUser.getEnterUserId(),pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("four_check.do")
    @ResponseBody
    public ServerResponse fourCheckOrder(HttpSession session,int checkOrderId,int lvId,int status ){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if (user.getRole()==1||user.getRole()==0){
            return ServerResponse.createByErrorMessage("该用户没有审核权限");
        }
        return iOrderService.fourCheckOrder(user.getId(),checkOrderId,lvId,status);
    }

    @RequestMapping("three_check.do")
    @ResponseBody
    public ServerResponse threeCheckOrder(HttpSession session,int checkOrderId,int lvId,int status ){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if (user.getRole()==1||user.getRole()==0){
            return ServerResponse.createByErrorMessage("该用户没有审核权限");
        }
        return iOrderService.threeCheckOrder(user.getId(),checkOrderId,lvId,status);
    }

    @RequestMapping("confirm.do")
    @ResponseBody
    public ServerResponse confirmOrder(HttpSession session,Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.confirmOrder(user.getId(),orderNo);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.confirmOrder(enterUser.getEnterUserId(),orderNo);
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.pay(orderNo,user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.pay(orderNo,enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }

    @RequestMapping("set_pay.do")
    @ResponseBody
    public ServerResponse setPay(HttpSession session, Long orderNo){

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.setPay(orderNo,user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.setPay(orderNo,enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");

    }


    //根据时间获取相关账单，有前端做成excel表让客户下载；

    @RequestMapping("select_order.do")
    @ResponseBody
    public ServerResponse selectOrder(HttpSession session ,
                                      @RequestParam(value = "year",defaultValue = "-1") int year,
                                      @RequestParam(value = "month" ,defaultValue = "-1") int month,
                                      @RequestParam(value = "startTime",defaultValue = "-1") String startTime,
                                      @RequestParam(value = "endTime",defaultValue = "-1") String endTime){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.selectOrder(user.getId(),year,month,startTime,endTime);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.selectOrder(enterUser.getEnterUserId(),year,month,startTime,endTime);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("query.do")
    @ResponseBody
    public ServerResponse query(String orderNo){
        return iOrderService.query(orderNo);
    }



}
