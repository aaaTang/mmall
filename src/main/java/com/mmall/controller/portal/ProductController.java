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
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required=false)String keyword,
                                         @RequestParam(value="categoryId",required=false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);

    }

    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse getList(@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        return iProductService.getProductList(pageNum,pageSize);
    }

    @RequestMapping("list_test.do")
    @ResponseBody
    public ServerResponse<ProductListTestVo> listTest(int categoryId){
        return iProductService.getProductListTest(categoryId);
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
            return iProductService.getCollect(user.getId(),pageNum,pageSize);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iProductService.getCollect(enterUser.getEnterUserId(),pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("query_collect.do")
    @ResponseBody
    public ServerResponse queryCollect(HttpSession session,int productId){
        if (session.getAttribute(Const.CURRENT_USER)==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String className=session.getAttribute(Const.CURRENT_USER).getClass().getName();
        if (className.equals("com.mmall.pojo.User")){
            User user=(User)session.getAttribute(Const.CURRENT_USER);
            return iProductService.queryCollect(user.getId(),productId);
        }
        if (className.equals("com.mmall.pojo.EnterUser")){
            EnterUser enterUser=(EnterUser)session.getAttribute(Const.CURRENT_USER);
            return iProductService.queryCollect(enterUser.getEnterUserId(),productId);
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }

    @RequestMapping("get_path.do")
    @ResponseBody
    public ServerResponse getPath(int productId){
        return iCategoryService.getProductPath(productId);
    }

}
