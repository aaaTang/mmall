package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IMessageService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/message")
public class MessageManageController {

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse addMessage(HttpSession session,String title,String content,String messageUrl,int goalUser){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if (iUserService.checkAdminRole(user).issuccess()){
            //添加分页
            return iMessageService.add(user.getUsername(),title,content,messageUrl,goalUser);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

}
