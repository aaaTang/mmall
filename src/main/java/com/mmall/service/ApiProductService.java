package com.mmall.service;

import com.alibaba.fastjson.JSONObject;

public interface ApiProductService {

    JSONObject getPools();

    JSONObject getSkus(String catalog_id);

    JSONObject getImages(String sku);

    JSONObject shelfStates(String sku);

    JSONObject getRatings(String sku);

    JSONObject getStocks(String sku,String area);

}
