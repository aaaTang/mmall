package com.mmall.controller.portal;


import com.mmall.common.ServerResponse;
import com.mmall.service.IHotProductService;
import com.mmall.vo.HotProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hot_product/")
public class HotProductController {

    @Autowired
    private IHotProductService iHotProductService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<HotProductListVo> detail(){
        return iHotProductService.getList();
    }
}
