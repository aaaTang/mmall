package com.mmall.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mmall.service.ApiProductService;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/product/")
public class ApiProductController {

    @Autowired
    private ApiProductService apiProductService;

    @RequestMapping(value="get_pools",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getPools(String token){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.getPools();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="skus",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getSkus(String token,String catalog_id){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.getSkus(catalog_id);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="images",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getImages(String token,String sku){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.getImages(sku);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="shelf_states",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject shelfStates(String token,String sku){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.shelfStates(sku);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="ratings",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getRatings(String token,String sku){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.getRatings(sku);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }


    @RequestMapping(value="stocks",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getStocks(String token,String sku,String area){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiProductService.getStocks(sku,area);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

}
