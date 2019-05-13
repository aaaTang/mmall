package com.mmall.controller.portal;

import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IUnionPayService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/unionPay/")
@Slf4j
public class UnionPayController {

    @Autowired
    private IUnionPayService iUnionPayService;

    @RequestMapping("pay.do")
    @ResponseBody
    public void pay(HttpServletRequest request, HttpServletResponse response, Long orderNo) throws IOException {
        String token= CookUtil.readLoginToken(request);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString!=null){
                User user= JsonUtil.string2Obj(userString,User.class);
                EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
                if (user.getId()!=null){
                    iUnionPayService.pay(request, response,orderNo,user.getId());
                }
                if (enterUser.getEnterUserId()!=null){
                    iUnionPayService.pay(request, response,orderNo,enterUser.getEnterUserId());
                }
            }
        }
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
