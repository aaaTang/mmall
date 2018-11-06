package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListTestVo;
import com.mmall.vo.ProductSugVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iProductService.getProductDetail(0,productId);
        }else {
            return iProductService.getProductDetail(user.getRole(),productId);
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,@RequestParam(value="keyword",required=false)String keyword,
                                         @RequestParam(value="categoryId",required=false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "")String orderBy){

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iProductService.getProductByKeywordCategory(0,keyword,categoryId,pageNum,pageSize,orderBy);
        }else {
            return iProductService.getProductByKeywordCategory(user.getRole(),keyword,categoryId,pageNum,pageSize,orderBy);
        }


    }

    @RequestMapping("list_keyword.do")
    @ResponseBody
    public ServerResponse<PageInfo> listByKeyword(HttpSession session,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "20") int pageSize,String keyword){

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iProductService.getProductListByKeyword(0,pageNum,pageSize,keyword);
        }else {
            return iProductService.getProductListByKeyword(user.getRole(),pageNum,pageSize,keyword);
        }
    }

    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iProductService.getProductList(0,pageNum,pageSize);
        }else {
            return iProductService.getProductList(user.getRole(),pageNum,pageSize);
        }
    }

    @RequestMapping("list_test.do")
    @ResponseBody
    public ServerResponse<PageInfo> listTest(HttpSession session,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "20") int pageSize,int categoryId){

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iProductService.getProductListTest(0,pageNum,pageSize,categoryId);
        }else {
            return iProductService.getProductListTest(user.getRole(),pageNum,pageSize,categoryId);
        }
    }

    @RequestMapping("get_sug.do")
    @ResponseBody
    public ServerResponse<List<ProductSugVo>> getSug(int categoryId){
        return iProductService.getProductSugList(categoryId);
    }

    @RequestMapping("love.do")
    @ResponseBody
    public ServerResponse<List<ProductSugVo>> getLove(){
        return iProductService.getProductLoveList();
    }

    @RequestMapping("add_collect.do")
    @ResponseBody
    public ServerResponse addCollect(HttpSession session,int productId){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iProductService.addCollect(user.getId(),productId);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iProductService.addCollect(enterUser.getEnterUserId(),productId);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("delete_collect.do")
    @ResponseBody
    public ServerResponse deleteCollect(HttpSession session,int productId){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iProductService.delectCollect(user.getId(),productId);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iProductService.delectCollect(enterUser.getEnterUserId(),productId);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("get_collect.do")
    @ResponseBody
    public ServerResponse getCollect(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iProductService.getCollect(user.getRole(),user.getId(),pageNum,pageSize);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iProductService.getCollect(0,enterUser.getEnterUserId(),pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("query_collect.do")
    @ResponseBody
    public ServerResponse queryCollect(HttpSession session,int productId){
//        if (session.getAttribute(Const.CURRENT_USER)==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
//        if (className.equals("com.mmall.pojo.User")){
//            User user=(User)session.getAttribute(Const.CURRENT_USER);
//            return iProductService.queryCollect(user.getId(),productId);
//        }
//        if (className.equals("com.mmall.pojo.EnterUser")){
//            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
//            return iProductService.queryCollect(enterUser.getEnterUserId(),productId);
//        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("get_path.do")
    @ResponseBody
    public ServerResponse getPath(int productId){
        return iCategoryService.getProductPath(productId);
    }

    @RequestMapping("get_all_product.do")
    @ResponseBody
    public ServerResponse getAllProdcut(){
        return iProductService.getAllProduct();
    }


}
