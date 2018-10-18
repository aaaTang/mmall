package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotProduct;
import com.mmall.pojo.User;
import com.mmall.service.IHotProductService;
import com.mmall.vo.HotProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/hot_product/")
public class HotProductController {

    @Autowired
    private IHotProductService iHotProductService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<HotProductListVo> detail(HttpSession session){

        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return iHotProductService.getList(0);
        }else {
            return iHotProductService.getList(user.getRole());
        }
    }
}
