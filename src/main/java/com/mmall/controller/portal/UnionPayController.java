package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IUnionPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/unionPay/")
@Slf4j
public class UnionPayController {

    @Autowired
    private IUnionPayService iUnionPayService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpServletRequest request, HttpServletResponse response, HttpSession session, Long orderNo) throws IOException {

        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iUnionPayService.pay(request, response,orderNo,user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iUnionPayService.pay(request, response,orderNo,enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping(value = "test.do")
    @ResponseBody
    public void payTest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        iUnionPayService.payTest(request, response);
    }

    @RequestMapping(value = "successRedict")
    @ResponseBody
    public void successRedict(HttpServletRequest request, HttpServletResponse response) throws IOException {
        iUnionPayService.successRedict(request, response);
    }

    @RequestMapping(value = "frontRcvResponse")
    @ResponseBody
    public void frontRcvResponse(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        iUnionPayService.frontRcvResponse(request, response);

    }

    @RequestMapping(value = "backRcvResponse")
    @ResponseBody
    public void backRcvResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        iUnionPayService.backRcvResponse(request, response);

    }

}
