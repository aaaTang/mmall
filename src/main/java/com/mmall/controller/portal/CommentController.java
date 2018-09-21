package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICommentService;
import com.mmall.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/comment/")
@Slf4j
public class CommentController {

    @Autowired
    private ICommentService iCommentService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, int productId, @RequestParam(value = "typeId",defaultValue = "-1")int typeId,Long orderNo,int orderItemId, int starLevel, String content){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iCommentService.add(user.getId(),productId,typeId,orderNo,orderItemId,starLevel,content);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iCommentService.add(enterUser.getEnterUserId(),productId,typeId,orderNo,orderItemId,starLevel,content);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<List<CommentVo>> list(Integer productId){
        return iCommentService.list(productId);
    }

    @RequestMapping("add_newcontent.do")
    @ResponseBody
    public ServerResponse addNewContent(HttpSession session, int commentId,String newContent){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iCommentService.addNewContent(user.getId(),commentId,newContent);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iCommentService.addNewContent(enterUser.getEnterUserId(),commentId,newContent);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

}
