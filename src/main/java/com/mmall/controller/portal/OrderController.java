package com.mmall.controller.portal;

import com.alipay.api.internal.util.AlipaySignature;
import com.mmall.common.AlipayConfig;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IDzfpService;
import com.mmall.service.IOrderService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    private IDzfpService iDzfpService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest, Integer shippingId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.createOrder(user.getId(),shippingId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.createOrder(enterUser.getEnterUserId(),shippingId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("quick_create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest, Integer productId,@RequestParam(value = "modelId",defaultValue = "0")Integer modelId,Integer count,Integer shippingId,Integer paymentType){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.quickCreateOrder(user.getId(),productId,modelId,count,shippingId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.quickCreateOrder(enterUser.getEnterUserId(),productId,modelId,count,shippingId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest, Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.cancel(user.getId(),orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.cancel(enterUser.getEnterUserId(),orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest httpServletRequest, Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.delete(user.getId(),orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.delete(enterUser.getEnterUserId(),orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.getOrderCartProduct(user.getId());
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.getOrderCartProduct(enterUser.getEnterUserId());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest httpServletRequest,Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.getOrderDetail(user.getId(),orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.getOrderDetail(enterUser.getEnterUserId(),orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.getOrderList(enterUser.getEnterUserId(),pageNum,pageSize);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("submit.do")
    @ResponseBody
    public ServerResponse submit(HttpServletRequest httpServletRequest, Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.submit(user.getId(), orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.submit(enterUser.getEnterUserId(), orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("get_check.do")
    @ResponseBody
    public ServerResponse getCheckList(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.getCheckList(user.getId(),pageNum,pageSize);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.getCheckList(enterUser.getEnterUserId(),pageNum,pageSize);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("check.do")
    @ResponseBody
    public ServerResponse fourCheckOrder(HttpServletRequest httpServletRequest,int checkOrderId,int lvId,int status,String checkOption ){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                if (user.getRole()==1||user.getRole()==0){
                    log.info(String.valueOf(user.getRole()));
                    return ServerResponse.createByErrorMessage("该用户没有审核权限");
                }
                if(status==Const.CheckStatusEnum.FAIL_CHECK.getCode() & org.apache.commons.lang3.StringUtils.isEmpty(checkOption)){
                    return ServerResponse.createByErrorMessage("审核不通过请填写审核意见");
                }
                return iOrderService.fourCheckOrder(user.getId(),checkOrderId,lvId,status,checkOption);
            }
            if (enterUser.getEnterUserId()!=null){
                return ServerResponse.createByErrorMessage("您没有审核权限功能；");
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("three_check.do")
    @ResponseBody
    public ServerResponse threeCheckOrder(HttpServletRequest httpServletRequest,int checkOrderId,int lvId,int status ){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                if (user.getRole()==1||user.getRole()==0){
                    return ServerResponse.createByErrorMessage("该用户没有审核权限");
                }
                return iOrderService.threeCheckOrder(user.getId(),checkOrderId,lvId,status);
            }
            if (enterUser.getEnterUserId()!=null){
                return ServerResponse.createByErrorMessage("您没有审核权限功能；");
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("confirm.do")
    @ResponseBody
    public ServerResponse confirmOrder(HttpServletRequest httpServletRequest,Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.confirmOrder(user.getId(),orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.confirmOrder(enterUser.getEnterUserId(),orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpServletRequest httpServletRequest, Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.pay(orderNo,user.getId());
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.pay(orderNo,enterUser.getEnterUserId());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("set_pay.do")
    @ResponseBody
    public ServerResponse setPay(HttpServletRequest httpServletRequest, Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.setPay(orderNo,user.getId());
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.setPay(orderNo,enterUser.getEnterUserId());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }


    //根据时间获取相关账单，有前端做成excel表让客户下载；

    @RequestMapping("select_order.do")
    @ResponseBody
    public ServerResponse selectOrder(HttpServletRequest httpServletRequest ,
                                      @RequestParam(value = "year",defaultValue = "-1") int year,
                                      @RequestParam(value = "month" ,defaultValue = "-1") int month,
                                      @RequestParam(value = "startTime",defaultValue = "-1") String startTime,
                                      @RequestParam(value = "endTime",defaultValue = "-1") String endTime){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.selectOrder(user.getId(),year,month,startTime,endTime);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.selectOrder(enterUser.getEnterUserId(),year,month,startTime,endTime);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("create_drawback.do")
    @ResponseBody
    public ServerResponse createDrawback(HttpServletRequest httpServletRequest, Long orderNo, int type, BigDecimal money, int reason, int refund_way, String description ){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.drawback(user.getId(),orderNo,type,money,reason,refund_way,description);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.drawback(enterUser.getEnterUserId(),orderNo,type,money,reason,refund_way,description);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("fpkj.do")
    @ResponseBody
    public ServerResponse fpkj(HttpServletRequest httpServletRequest, Long orderNo, String companyName,@RequestParam(value = "tax" ,defaultValue = "") String tax ){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.kjfp(orderNo,companyName,tax);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.kjfp(orderNo,companyName,tax);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("fp_sendmail.do")
    @ResponseBody
    public ServerResponse sendMail(HttpServletRequest httpServletRequest, String mail,Long orderNo){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.sendMail(mail,orderNo);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.sendMail(mail,orderNo);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("fp_list.do")
    @ResponseBody
    public ServerResponse fpList(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iOrderService.fpList(user.getId(),pageNum,pageSize);
            }
            if (enterUser.getEnterUserId()!=null){
                return iOrderService.fpList(enterUser.getEnterUserId(),pageNum,pageSize);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("alipayNotifyNotice.do")
    @ResponseBody
    public String alipayNotifyNotice(HttpServletRequest request) throws Exception {

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
                // 修改叮当状态，改为 支付成功，已付款; 同时新增支付流
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
