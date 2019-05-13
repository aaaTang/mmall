package com.mmall.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mmall.service.ApiAreaService;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/area/")

public class ApiAreaController {

    @Autowired
    private ApiAreaService apiAreaService;

    @RequestMapping(value="provinces",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getProvinces(String token){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiAreaService.getProvinces();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="cities",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getCities(String token,Integer id){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiAreaService.getCities(id);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

    @RequestMapping(value="getCounty",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getCounty(String token,Integer id){
        String result= RedisPoolUtil.get(token);
        if (result!=null){
            return apiAreaService.getCounty(id);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("desc","token_expired");
        return jsonObject;
    }

}
