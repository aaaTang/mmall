package com.mmall.service;

import com.alibaba.fastjson.JSONObject;

public interface ApiOrderService {

    JSONObject submit(String sku);

}
