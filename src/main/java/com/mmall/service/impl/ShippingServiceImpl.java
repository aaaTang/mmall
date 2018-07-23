package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import com.mmall.vo.ShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId,Shipping shipping,String status){
        shipping.setUserId(userId);
        if (status.equals("true")){
            shipping.setStatus(1);
            Shipping defaultshipping=shippingMapper.selectDefault(userId);
            if (defaultshipping!=null){
                defaultshipping.setStatus(0);
                shippingMapper.updateStatus(defaultshipping);
            }
        }else {
            shipping.setStatus(0);
        }
        int rowCount=shippingMapper.insert(shipping);
        if (rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId,Shipping shipping, String status){
        shipping.setUserId(userId);
        if (status.equals("true")){
            shipping.setStatus(1);
            Shipping defaultshipping=shippingMapper.selectDefault(userId);
            if (defaultshipping!=null){
                defaultshipping.setStatus(0);
                shippingMapper.updateStatus(defaultshipping);
            }
        }else {
            shipping.setStatus(0);
        }
        int rowCount=shippingMapper.updateByShipping(shipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){

        Shipping shipping=shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return  ServerResponse.createBySuccess("查询地址成功",shipping);

    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        List<ShippingVo> shippingVoList=assembleShippingVoList(shippingList);
        PageInfo pageInfo=new PageInfo(shippingVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<ShippingVo> assembleShippingVoList(List<Shipping> shippingList){
        List<ShippingVo> shippingVoList=Lists.newArrayList();
        for (Shipping shipping:shippingList){
            ShippingVo shippingVo=new ShippingVo();
            shippingVo.setId(shipping.getId());
            shippingVo.setUserId(shipping.getUserId());
            shippingVo.setReceiverName(shipping.getReceiverName());
            shippingVo.setReceiverPhone(shipping.getReceiverPhone());
            shippingVo.setReceiverMobile(shipping.getReceiverMobile());
            shippingVo.setReceiverProvince(shipping.getReceiverProvince());
            shippingVo.setReceiverCity(shipping.getReceiverCity());
            shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVo.setReceiverAddress(shipping.getReceiverAddress());
            shippingVo.setReceiverZip(shipping.getReceiverZip());
            if (shipping.getStatus()==1){
                shippingVo.setStatus(true);
            }else{
                shippingVo.setStatus(false);
            }
            shippingVoList.add(shippingVo);
        }
        return shippingVoList;
    }

    public ServerResponse setDefault(Integer userId,int shippingId){
        Shipping shipping=shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping!=null){
            Shipping defaultShipping=shippingMapper.selectDefault(userId);
            if (defaultShipping!=null){
                defaultShipping.setStatus(0);
                shippingMapper.updateStatus(defaultShipping);
            }

            shipping.setUserId(userId);
            shipping.setStatus(1);
            int rowCount=shippingMapper.updateStatus(shipping);
            if (rowCount > 0){
                return ServerResponse.createBySuccess("设置默认地址成功");
            }
            return ServerResponse.createByErrorMessage("设置默认地址失败");
        }else{
            return ServerResponse.createByErrorMessage("该地址不存在");
        }
    }

}
