package com.mmall.controller.company;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.service.IEnterUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/enter/user")
public class EnterUserController {

    @Autowired
    private IEnterUserService iEnterUserService;

    /**
     *用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody

    public ServerResponse<EnterUser> login(HttpServletResponse httpServletResponse, String username, String password, HttpSession session){

        ServerResponse<EnterUser> response=iEnterUserService.login(username,password);

        if (response.issuccess()){
            CookUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String token=CookUtil.readLoginToken(httpServletRequest);
        CookUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(token);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iEnterUserService.checkValid(str,type);
    }


    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<EnterUser> getUserInfo(HttpServletRequest httpServletRequest){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (StringUtils.isNotBlank(userString)){
                EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
                if (enterUser.getEnterUserId()!=null){
                    return ServerResponse.createBySuccess(enterUser);
                }
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String passwordOld,String passwordNew){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (StringUtils.isNotBlank(userString)){
                EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
                if (enterUser.getEnterUserId()!=null){
                    return iEnterUserService.resetPassword(passwordOld,passwordNew,enterUser);
                }
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<EnterUser> update_information(HttpServletRequest httpServletRequest,EnterUser enterUser){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (StringUtils.isNotBlank(userString)){
                EnterUser currentUser=JsonUtil.string2Obj(userString,EnterUser.class);
                if (enterUser.getEnterUserId()!=null){
                    enterUser.setEnterUserId(currentUser.getEnterUserId());
                    enterUser.setEnterName(currentUser.getEnterName());
                    ServerResponse<EnterUser> response=iEnterUserService.updateInformation(enterUser);
                    if (response.issuccess()){
                        RedisPoolUtil.setEx(token, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
                    }
                    return response;
                }
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }



}
