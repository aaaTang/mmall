package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.math.BigDecimal;

public interface IDzfpService {

    ServerResponse searchProduct(String productName);

    ServerResponse addProduct(Integer id,Integer productId,String productName,String unit,String model,Float count,BigDecimal price);

    ServerResponse deleteProduct(Integer productId);

    ServerResponse kjfp(String companyName, String tax,Integer kplx,String ylsh);

    ServerResponse listProduct();

    ServerResponse fpxz(String lsh) throws Exception;

    ServerResponse sendMail(String mail,Long orderNo);

    ServerResponse fpList(Integer pageNum,Integer pageSize);

}
