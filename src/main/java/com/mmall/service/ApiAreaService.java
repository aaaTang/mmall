package com.mmall.service;

import com.alibaba.fastjson.JSONObject;

public interface ApiAreaService {

    JSONObject getProvinces();

    JSONObject getCities(Integer id);

    JSONObject getCounty(Integer id);

}
