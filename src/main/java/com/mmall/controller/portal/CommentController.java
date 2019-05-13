package com.mmall.controller.portal;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICommentService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/comment/")
@Slf4j
public class CommentController {

    @Autowired
    private ICommentService iCommentService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, int productId, @RequestParam(value = "typeId",defaultValue = "-1")int typeId, Long orderNo, int orderItemId, int starLevel, String content){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCommentService.add(user.getId(),productId,typeId,orderNo,orderItemId,starLevel,content);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCommentService.add(enterUser.getEnterUserId(),productId,typeId,orderNo,orderItemId,starLevel,content);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<List<CommentVo>> list(Integer productId){
        return iCommentService.list(productId);
    }

    @RequestMapping("add_newcontent.do")
    @ResponseBody
    public ServerResponse addNewContent(HttpServletRequest httpServletRequest, int commentId,String newContent){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCommentService.addNewContent(user.getId(),commentId,newContent);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCommentService.addNewContent(enterUser.getEnterUserId(),commentId,newContent);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
}
