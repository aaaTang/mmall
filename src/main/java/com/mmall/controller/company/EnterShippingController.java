package com.mmall.controller.company;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterShipping;
import com.mmall.pojo.EnterUser;
import com.mmall.service.IEnterShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/enter/shipping")
public class EnterShippingController {

    @Autowired
    private IEnterShippingService iEnterShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, EnterShipping enterShipping, @RequestParam(value = "status1",defaultValue = "false") String status){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iEnterShippingService.add(enterUser.getEnterUserId(),enterShipping,status);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest httpServletRequest,Integer shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iEnterShippingService.delete(enterUser.getEnterUserId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest, EnterShipping enterShipping,@RequestParam(value = "status1") String status){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iEnterShippingService.update(enterUser.getEnterUserId(),enterShipping,status);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpServletRequest httpServletRequest,Integer shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iEnterShippingService.select(enterUser.getEnterUserId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iEnterShippingService.list(enterUser.getEnterUserId(),pageNum,pageSize);
    }

    @RequestMapping("set_default.do")
    @ResponseBody
    public ServerResponse setDefault(HttpServletRequest httpServletRequest,int shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iEnterShippingService.setDefault(enterUser.getEnterUserId(),shippingId);
    }

}
