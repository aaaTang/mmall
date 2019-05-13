package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotProduct;
import com.mmall.pojo.User;
import com.mmall.service.IHotProductService;
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
@RequestMapping("/manage/hot_product")
public class HotProductManageController {

    @Autowired
    private IHotProductService iHotProductService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, HotProduct hotproduct){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iHotProductService.saveOrUpdateProduct(hotproduct);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

}
