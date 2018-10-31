package com.mmall.controller.company;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.service.IEnterUserService;
import com.mmall.util.CookieUtil;
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

    public ServerResponse<EnterUser> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){

        ServerResponse<EnterUser> response=iEnterUserService.login(username,password);

        if (response.issuccess()){
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
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

        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);

        if (enterUser!=null){
            return ServerResponse.createBySuccess(enterUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String passwordOld,String passwordNew){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser enterUser=JsonUtil.string2Obj(userStr,EnterUser.class);
        if (enterUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iEnterUserService.resetPassword(passwordOld,passwordNew,enterUser);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<EnterUser> update_information(HttpServletRequest httpServletRequest,EnterUser enterUser){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        EnterUser currentUser=JsonUtil.string2Obj(userStr,EnterUser.class);

        if (currentUser==null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        enterUser.setEnterUserId(currentUser.getEnterUserId());
        enterUser.setEnterName(currentUser.getEnterName());
        ServerResponse<EnterUser> response=iEnterUserService.updateInformation(enterUser);
        if (response.issuccess()){
            return ServerResponse.createBySuccess(response.getData());
        }
        return ServerResponse.createByErrorMessage("更新失败！");
    }



}
