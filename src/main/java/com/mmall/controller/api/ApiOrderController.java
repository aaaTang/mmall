package com.mmall.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mmall.service.ApiOrderService;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/order/")

public class ApiOrderController {

    @Autowired
    private ApiOrderService apiOrderService;

    @RequestMapping(value="submit",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject submit(String token,String sku){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiOrderService.submit(sku);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }


}
