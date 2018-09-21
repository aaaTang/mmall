package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/message/")
public class MessageController {

    @Autowired
    private IMessageService iMessageService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10") int pageSize,
                                         HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.list(pageNum,pageSize,user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.list(pageNum,pageSize,enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("getNum.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.getNum(user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.getNum(enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("set_read.do")
    @ResponseBody
    public ServerResponse<PageInfo> setRead(HttpSession session,@RequestParam(value = "id",defaultValue = "0") int id){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.setRead(user.getId(),id);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.setRead(enterUser.getEnterUserId(),id);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("deleteAll.do")
    @ResponseBody
    public ServerResponse<PageInfo> deleteAll(HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.deleteAll(user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.deleteAll(enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("deleteRead.do")
    @ResponseBody
    public ServerResponse<PageInfo> deleteRead(HttpSession session){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.deleteRead(user.getId());
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iMessageService.deleteRead(enterUser.getEnterUserId());
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

}
