package com.mmall.controller.portal;

import com.alipay.api.internal.util.AlipaySignature;
import com.mmall.common.AlipayConfig;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    public ServerResponse create(HttpSession session, Integer productId,@RequestParam(value = "modelId",defaultValue = "0")Integer modelId,Integer count,Integer shippingId,Integer paymentType){

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

    @RequestMapping("check.do")
    @ResponseBody
    public ServerResponse fourCheckOrder(HttpSession session,int checkOrderId,int lvId,int status ){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if (user.getRole()==1||user.getRole()==0){
            log.info(String.valueOf(user.getRole()));
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

    @RequestMapping("create_drawback.do")
    @ResponseBody
    public ServerResponse createDrawback(HttpSession session, Long orderNo, int type, BigDecimal money, int reason, int refund_way, String description ){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.drawback(user.getId(),orderNo,type,money,reason,refund_way,description);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iOrderService.drawback(enterUser.getEnterUserId(),orderNo,type,money,reason,refund_way,description);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }


    @RequestMapping("alipayNotifyNotice.do")
    @ResponseBody
    public String alipayNotifyNotice(HttpServletRequest request, HttpServletRequest response) throws Exception {

        log.info("支付成功, 进入异步通知接口...");

        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——

		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
        if(signVerified) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意： 尚自习的订单没有退款功能, 这个条件判断是进不来的, 所以此处不必写代码
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知

                // 修改叮当状态，改为 支付成功，已付款; 同时新增支付流水


            }
            iOrderService.setPayStatus(out_trade_no);
            System.out.println("支付成功...");
            log.info("支付成功...");

        }else {//验证失败
            log.info("支付, 验签失败...");
        }

        return "success";
    }
}
