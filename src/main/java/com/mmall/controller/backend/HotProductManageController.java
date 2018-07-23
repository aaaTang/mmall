package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotProduct;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IHotProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/hot_product")
public class HotProductManageController {

    @Autowired
    private IHotProductService iHotProductService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, HotProduct hotproduct){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if (iUserService.checkAdminRole(user).issuccess()){
            return iHotProductService.saveOrUpdateProduct(hotproduct);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

}
