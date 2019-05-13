package com.mmall.service.impl;

import net.sf.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmall.service.ApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("apiOrderService")
@Slf4j
public class ApiOrderServiceImpl implements ApiOrderService{
    @Override
    public JSONObject submit(String sku) {
        System.out.println(sku);
        //sku=sku.substring(1,sku.length()-1);
        JSONArray jsonArray = JSONArray.fromObject(sku);
        System.out.println(jsonArray);
        return null;

    }
}
