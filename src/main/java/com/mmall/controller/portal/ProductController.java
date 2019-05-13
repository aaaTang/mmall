package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IFloorService;
import com.mmall.service.IProductService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.PageInfoAndBrandVo;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductSugVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IFloorService iFloorService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
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

    @RequestMapping("list_keyword.do")
    @ResponseBody
    public ServerResponse<PageInfoAndBrandVo> listByKeyword(@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "20") int pageSize,String keyword){
        return iProductService.getProductListByKeyword(pageNum,pageSize,keyword);
    }

    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse getList(@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        return iProductService.getProductList(pageNum,pageSize);
       
    }

    @RequestMapping("list_test.do")
    @ResponseBody
    public ServerResponse<PageInfoAndBrandVo> listTest( @RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "20") int pageSize, int categoryId){
        return iProductService.getProductListTest(pageNum,pageSize,categoryId);
    }

    @RequestMapping("list_brand.do")
    @ResponseBody
    public ServerResponse<PageInfo> listBrand(@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "20") int pageSize, Integer categoryId,String keyword,String brand){

        if (categoryId!=null){
            return iProductService.getProductByCategoryIdAndBrand(pageNum,pageSize,categoryId,brand);
        }
        return iProductService.getProductByKeywordIdAndBrand(pageNum,pageSize,keyword,brand);
        
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
    public ServerResponse addCollect(HttpServletRequest httpServletRequest, int productId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iProductService.addCollect(user.getId(),productId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iProductService.addCollect(enterUser.getEnterUserId(),productId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete_collect.do")
    @ResponseBody
    public ServerResponse deleteCollect(HttpServletRequest httpServletRequest,int productId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iProductService.delectCollect(user.getId(),productId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iProductService.delectCollect(enterUser.getEnterUserId(),productId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("get_collect.do")
    @ResponseBody
    public ServerResponse getCollect(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iProductService.getCollect(user.getRole(),user.getId(),pageNum,pageSize);
            }
            if (enterUser.getEnterUserId()!=null){
                return iProductService.getCollect(0,enterUser.getEnterUserId(),pageNum,pageSize);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
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

    @RequestMapping("select_null.do")
    @ResponseBody
    public ServerResponse selectNull(){
        return iProductService.selectNull();
    }

    @RequestMapping("list_floor.do")
    @ResponseBody
    public ServerResponse listFloor(){
        return iFloorService.listFloorSort();
    }
}
