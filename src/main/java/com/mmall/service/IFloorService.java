package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.FloorCategory;

public interface IFloorService {

    ServerResponse createFloor(FloorCategory floorCategory);

    ServerResponse updateFloor(FloorCategory floorCategory);

    ServerResponse deleteFloor(Integer floorId);

    ServerResponse listFloorSort();

    ServerResponse addProduct(Integer floorId,Integer categoryId,Integer productId);

    ServerResponse deleteProduct(Integer floorProductId);

    ServerResponse productList();

}
