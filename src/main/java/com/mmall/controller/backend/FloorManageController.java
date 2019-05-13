package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.FloorCategory;
import com.mmall.pojo.User;
import com.mmall.service.IFloorService;
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
@RequestMapping("/manage/floor")

public class FloorManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFloorService iFloorService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse createFloor(HttpServletRequest httpServletRequest, FloorCategory floorCategory){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.createFloor(floorCategory);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse updateFloor(HttpServletRequest httpServletRequest,FloorCategory floorCategory){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.updateFloor(floorCategory);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse deleteFloor(HttpServletRequest httpServletRequest,Integer floorId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.deleteFloor(floorId);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse listFloor(HttpServletRequest httpServletRequest){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.listFloorSort();
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("product_list.do")
    @ResponseBody
    public ServerResponse productList(HttpServletRequest httpServletRequest){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.productList();
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("add_product.do")
    @ResponseBody
    public ServerResponse addProduct(HttpServletRequest httpServletRequest,Integer floorId,Integer categoryId,Integer productId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.addProduct(floorId,categoryId,productId);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpServletRequest httpServletRequest,Integer floorProductId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iFloorService.deleteProduct(floorProductId);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

    }

}
