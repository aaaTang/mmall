package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId,Integer modelId);

    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count,Integer modelId);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds,Integer modelId);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer modelId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
