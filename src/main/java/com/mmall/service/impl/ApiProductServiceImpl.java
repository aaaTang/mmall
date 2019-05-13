package com.mmall.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.SkusMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.pojo.Skus;
import com.mmall.service.ApiProductService;
import com.mmall.vo.CategoryPoolVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("apiProductService")
@Slf4j
public class ApiProductServiceImpl implements ApiProductService{

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SkusMapper skusMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public JSONObject getPools() {
        JSONObject jsonObject = new JSONObject();
        List<Category> categoryList=categoryMapper.getApiCategory();
        List<CategoryPoolVo> categoryPoolVoList=new ArrayList<>();
        for(Category category:categoryList){
            CategoryPoolVo categoryPoolVo=new CategoryPoolVo();
            categoryPoolVo.setId(category.getCategoryCode());
            categoryPoolVo.setName(category.getCategoryName());
            categoryPoolVoList.add(categoryPoolVo);
        }
        jsonObject.put("success",true);
        jsonObject.put("result",categoryPoolVoList);

        return jsonObject;
    }

    @Override
    public JSONObject getSkus(String catalog_id) {
        JSONObject jsonObject = new JSONObject();
        Category category=categoryMapper.selectByCategoryCode(catalog_id);
        List<Skus> skusList=skusMapper.selectByCategoryId(category.getCategoryId());
        List<String> skuList=new ArrayList<>();
        for (Skus skus:skusList){
            skuList.add(String.valueOf(skus.getSku()));
        }
        jsonObject.put("success",true);
        jsonObject.put("result",skuList);
        return jsonObject;
    }

    @Override
    public JSONObject getImages(String sku) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);
        String[] skuList=sku.split(Const.skuSplit);

        List<JSONObject> resultJSONObject=Lists.newArrayList();

        for (String splitSku:skuList){
            JSONObject skuJsonObject = new JSONObject();
            skuJsonObject.put("sku",splitSku);

            Product product=productMapper.selectByPrimaryKey(Integer.valueOf(splitSku));

            List<JSONObject> imageJsonObjectList= Lists.newArrayList();
            String[] images=product.getBigImages().split(",");
            int count=0;
            for(String image:images){
                JSONObject imageJsonObject = new JSONObject();
                imageJsonObject.put("path",image);
                imageJsonObject.put("order",count);
                imageJsonObjectList.add(imageJsonObject);
                count=count+1;
            }
            skuJsonObject.put("images",imageJsonObjectList);

            resultJSONObject.add(skuJsonObject);
        }
        jsonObject.put("result",resultJSONObject);
        return jsonObject;
    }

    @Override
    public JSONObject shelfStates(String sku) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);
        String[] skuList=sku.split(Const.skuSplit);

        List<JSONObject> resultJSONObject=Lists.newArrayList();

        for (String splitSku:skuList) {
            JSONObject skuJsonObject = new JSONObject();
            skuJsonObject.put("sku", splitSku);
            Product product=productMapper.selectByPrimaryKey(Integer.valueOf(splitSku));
            skuJsonObject.put("state",product.getStatus());
            resultJSONObject.add(skuJsonObject);
        }
        jsonObject.put("result",resultJSONObject);
        return jsonObject;
    }

    @Override
    public JSONObject getRatings(String sku) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);
        String[] skuList=sku.split(Const.skuSplit);

        List<JSONObject> resultJSONObject=Lists.newArrayList();

        for (String splitSku:skuList) {
            JSONObject skuJsonObject = new JSONObject();
            skuJsonObject.put("average",Const.average);
            skuJsonObject.put("medium",Const.medium);
            skuJsonObject.put("good",Const.good);
            skuJsonObject.put("sku",splitSku);
            skuJsonObject.put("bad",Const.bad);

            resultJSONObject.add(skuJsonObject);
        }
        jsonObject.put("result",resultJSONObject);
        return jsonObject;
    }

    @Override
    public JSONObject getStocks(String sku, String area) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);

        String[] skuList=sku.split(Const.skuSplit);

        List<JSONObject> resultJSONObject=Lists.newArrayList();

        for (String splitSku:skuList) {
            JSONObject skuJsonObject = new JSONObject();
            Product product=productMapper.selectByPrimaryKey(Integer.valueOf(splitSku));

            skuJsonObject.put("area",area);
            skuJsonObject.put("sku",splitSku);
            if (product.getStock()>0){
                skuJsonObject.put("desc","有货");
                skuJsonObject.put("num",product.getStock());
            }else{
                skuJsonObject.put("desc","无货");
                skuJsonObject.put("num",0);
            }
            resultJSONObject.add(skuJsonObject);
        }
        jsonObject.put("result",resultJSONObject);
        return jsonObject;
    }
}
