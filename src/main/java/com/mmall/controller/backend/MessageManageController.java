package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IMessageService;
import com.mmall.service.IUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/message")
public class MessageManageController {

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse addMessage(HttpServletRequest httpServletRequest, String title, String content, String messageUrl, int goalUser){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    //添加分页
                    return iMessageService.add(user.getUsername(),title,content,messageUrl,goalUser);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

}
