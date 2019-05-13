package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IEnterUserService;
import com.mmall.service.IUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IEnterUserService iEnterUserService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletResponse httpServletResponse, HttpSession session){

        ServerResponse<User> response=iUserService.login(username,password);
        if (response.issuccess()){
            User user=response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                CookUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
            }
            else{
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String token=CookUtil.readLoginToken(httpServletRequest);
        CookUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(token);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="user_list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> userList(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iUserService.getUserList(pageNum,pageSize);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

    }

    @RequestMapping(value="enteruser_list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> enterUserList(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iEnterUserService.getAllEnterUser(pageNum,pageSize);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

}
