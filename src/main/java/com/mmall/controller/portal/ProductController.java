package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListTestVo;
import com.mmall.vo.ProductListVo;
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

}
