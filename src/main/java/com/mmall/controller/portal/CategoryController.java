package com.mmall.controller.portal;


import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/category/")
public class CategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IUserService iUserService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return iCategoryService.getList();
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iCategoryService.getEnterList();
                }
                return iCategoryService.getList();
            }
            if (enterUser.getEnterUserId()!=null){
                return iCategoryService.getEnterList();
            }
            return iCategoryService.getList();
        }
        return iCategoryService.getList();
    }

}
