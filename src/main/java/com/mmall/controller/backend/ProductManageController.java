package com.mmall.controller.backend;


import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.ProductModel;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
@Slf4j
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, Product product){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iProductService.saveOrUpdateProduct(product);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody

    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest, Integer productId,Integer status){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iProductService.setSaleStatus(productId,status);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

//    @RequestMapping("detail.do")
//    @ResponseBody
//
//    public ServerResponse getDetail(HttpServletRequest httpServletRequest, Integer productId){
//        User user=(User)session.getAttribute(Const.CURRENT_USER);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
//        }
//        if (iUserService.checkAdminRole(user).issuccess()){
//            //填充我们获取产品详情的业务逻辑
//            return iProductService.manageProductDetail(productId);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
//    }

    @RequestMapping("list_category.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest httpServletRequest,Integer zb, Integer categoryId,@RequestParam(value="pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "15") int pageSize){

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
                    return iProductService.getProduct(zb,categoryId,pageNum,pageSize);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("list_productmodel.do")
    @ResponseBody
    public ServerResponse getProductModel(HttpServletRequest httpServletRequest,Integer productId,@RequestParam(value="pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "15") int pageSize){

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
                    return iProductService.getProductModel(productId,pageNum,pageSize);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("save_productmodel.do")
    @ResponseBody
    public ServerResponse saveProductModel(HttpServletRequest httpServletRequest,ProductModel productModel){

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
                    return iProductService.saveProductModel(productModel);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete_productmodel.do")
    @ResponseBody
    public ServerResponse deleteProductModel(HttpServletRequest httpServletRequest,Integer id){

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
                    return iProductService.deleteProductModel(id);
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
    public ServerResponse updateProduct(HttpServletRequest httpServletRequest,Integer productId, String name, BigDecimal sprice, BigDecimal price,String brand,Integer status){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iProductService.updateProduct(productId,name,sprice,price,brand,status);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("change_category.do")
    @ResponseBody
    public ServerResponse changeCa(HttpServletRequest httpServletRequest,Integer productId,Integer categoryId){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    return iProductService.changeCategory(productId,categoryId);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }



//    @RequestMapping("list.do")
//    @ResponseBody
//
//    public ServerResponse getList(HttpServletRequest httpServletRequest, @RequestParam(value="pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "10") int pageSize){
//        User user=(User)session.getAttribute(Const.CURRENT_USER);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
//        }
//        if (iUserService.checkAdminRole(user).issuccess()){
//            //添加分页
//            return iProductService.getProductList(pageNum,pageSize);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
//    }


//    @RequestMapping("search.do")
//    @ResponseBody
//
//    public ServerResponse productSearch(HttpServletRequest httpServletRequest,String productName,Integer productId, @RequestParam(value="pageNum",defaultValue = "1") int pageNum,@RequestParam(value="pageSize",defaultValue = "10") int pageSize){
//        User user=(User)session.getAttribute(Const.CURRENT_USER);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
//        }
//        if (iUserService.checkAdminRole(user).issuccess()){
//            //添加分页
//            return iProductService.searchProduct(user.getRole(),productName,productId,pageNum,pageSize);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
//    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest httpServletRequest,@RequestParam(value="upload_file",required=false) MultipartFile file, HttpServletRequest request){

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    String path=request.getSession().getServletContext().getRealPath("upload");
                    String targetFileName=iFileService.upload(file,path);
                    String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
                    Map fileMap= Maps.newHashMap();
                    fileMap.put("uri",targetFileName);
                    fileMap.put("url",url);
                    return ServerResponse.createBySuccess(fileMap);
                }else {
                    return ServerResponse.createByErrorMessage("无权限操作");
                }
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest httpServletRequest, @RequestParam(value="upload_file",required=false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap=Maps.newHashMap();

        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                resultMap.put("success",false);
                resultMap.put("msg","请登录管理员");
                return resultMap;
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                if (iUserService.checkAdminRole(user).issuccess()){
                    String path=request.getSession().getServletContext().getRealPath("upload");
                    String targetFileName=iFileService.upload(file,path);
                    if (StringUtils.isBlank(targetFileName)){
                        resultMap.put("success",false);
                        resultMap.put("msg","上传失败");
                        return resultMap;
                    }
                    String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
                    resultMap.put("success",true);
                    resultMap.put("msg","上传成功");
                    resultMap.put("file_path",url);
                    response.addHeader("Access-Control-Allow-Headers","X-File-Name");
                    return resultMap;
                }else {
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    return resultMap;
                }
            }
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        resultMap.put("success",false);
        resultMap.put("msg","请登录管理员");
        return resultMap;
    }


}
