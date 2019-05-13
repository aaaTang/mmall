package com.mmall.controller.company;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterShipping;
import com.mmall.pojo.EnterUser;
import com.mmall.service.IEnterShippingService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/enter/shipping")
public class EnterShippingController {

    @Autowired
    private IEnterShippingService iEnterShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, EnterShipping enterShipping, @RequestParam(value = "status1",defaultValue = "false") String status){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.add(enterUser.getEnterUserId(),enterShipping,status);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest httpServletRequest,Integer shippingId){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.delete(enterUser.getEnterUserId(),shippingId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest, EnterShipping enterShipping,@RequestParam(value = "status1") String status){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.update(enterUser.getEnterUserId(),enterShipping,status);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()); 
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpServletRequest httpServletRequest,Integer shippingId){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.select(enterUser.getEnterUserId(),shippingId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         HttpServletRequest httpServletRequest){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.list(enterUser.getEnterUserId(),pageNum,pageSize);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("set_default.do")
    @ResponseBody
    public ServerResponse setDefault(HttpServletRequest httpServletRequest,int shippingId){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            EnterUser enterUser= JsonUtil.string2Obj(userString,EnterUser.class);
            if (enterUser.getEnterUserId()!=null){
                return iEnterShippingService.setDefault(enterUser.getEnterUserId(),shippingId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
}
