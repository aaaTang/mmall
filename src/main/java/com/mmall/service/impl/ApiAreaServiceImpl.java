package com.mmall.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mmall.dao.AreaMapper;
import com.mmall.pojo.Area;
import com.mmall.service.ApiAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("apiAreaService")
@Slf4j
public class ApiAreaServiceImpl implements ApiAreaService{

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public JSONObject getProvinces() {
        JSONObject jsonObject = new JSONObject();

        List<Area> provincesList=areaMapper.getProvinces();
        JSONObject resultJsonObject = new JSONObject();

        for(Area area:provincesList){
            String areaName=area.getAreaname();
            resultJsonObject.put(areaName,area.getAreaid());
        }
        jsonObject.put("result",resultJsonObject);
        jsonObject.put("success",true);
        jsonObject.put("desc","");
        return jsonObject;
    }

    @Override
    public JSONObject getCities(Integer id) {
        JSONObject jsonObject = new JSONObject();

        List<Area> provincesList=areaMapper.getChildren(id);
        JSONObject resultJsonObject = new JSONObject();

        for(Area area:provincesList){
            String areaName=area.getAreaname();
            resultJsonObject.put(areaName,area.getAreaid());
        }
        jsonObject.put("result",resultJsonObject);
        jsonObject.put("success",true);
        jsonObject.put("desc","");
        return jsonObject;
    }

    @Override
    public JSONObject getCounty(Integer id) {
        JSONObject jsonObject = new JSONObject();

        List<Area> provincesList=areaMapper.getChildren(id);
        JSONObject resultJsonObject = new JSONObject();

        for(Area area:provincesList){
            String areaName=area.getAreaname();
            resultJsonObject.put(areaName,area.getAreaid());
        }
        jsonObject.put("result",resultJsonObject);
        jsonObject.put("success",true);
        jsonObject.put("desc","");
        return jsonObject;
    }
}
