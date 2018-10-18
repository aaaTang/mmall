package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotProduct;
import com.mmall.vo.HotProductListVo;

public interface IHotProductService {

    ServerResponse<HotProductListVo> getList(Integer role);

    ServerResponse saveOrUpdateProduct(HotProduct hotProduct);
}
