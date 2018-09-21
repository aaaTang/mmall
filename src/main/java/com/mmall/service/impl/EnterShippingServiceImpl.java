package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.EnterShippingMapper;
import com.mmall.pojo.EnterShipping;
import com.mmall.pojo.Shipping;
import com.mmall.service.IEnterShippingService;
import com.mmall.vo.EnterShippingVo;
import com.mmall.vo.ShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iEnterShippingService")
public class EnterShippingServiceImpl implements IEnterShippingService {

    @Autowired
    private EnterShippingMapper enterShippingMapper;

    public ServerResponse add(Integer userId, EnterShipping enterShipping, String status){
        enterShipping.setEnterUserId(userId);
        if (status.equals("true")){
            enterShipping.setEnterStatus(1);
            EnterShipping defaultshipping=enterShippingMapper.selectDefault(userId);
            if (defaultshipping!=null){
                defaultshipping.setEnterStatus(0);
                enterShippingMapper.updateStatus(defaultshipping);
            }
        }else {
            enterShipping.setEnterStatus(0);
        }
        int rowCount=enterShippingMapper.insert(enterShipping);
        if (rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",enterShippingMapper.selectMaxId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        int resultCount=enterShippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId,EnterShipping enterShipping, String status){
        enterShipping.setEnterUserId(userId);
        if (status.equals("true")){
            enterShipping.setEnterStatus(1);
            EnterShipping defaultshipping=enterShippingMapper.selectDefault(userId);
            if (defaultshipping!=null){
                defaultshipping.setEnterStatus(0);
                enterShippingMapper.updateStatus(defaultshipping);
            }
        }else {
            enterShipping.setEnterStatus(0);
        }
        int rowCount=enterShippingMapper.updateByShipping(enterShipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<EnterShipping> select(Integer userId,Integer shippingId){

        EnterShipping enterShipping=enterShippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (enterShipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return  ServerResponse.createBySuccess("查询地址成功",enterShipping);

    }

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<EnterShipping> enterShippingList=enterShippingMapper.selectByUserId(userId);
        List<EnterShippingVo> shippingVoList=assembleShippingVoList(enterShippingList);
        PageInfo pageInfo=new PageInfo(shippingVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<EnterShippingVo> assembleShippingVoList(List<EnterShipping> enterShippingList){
        List<EnterShippingVo> enterShippingVoList=Lists.newArrayList();
        for (EnterShipping enterShipping:enterShippingList){
            EnterShippingVo enterShippingVo=new EnterShippingVo();
            enterShippingVo.setEnterShippingId(enterShipping.getEnterShippingId());
            enterShippingVo.setEnterUserId(enterShipping.getEnterUserId());
            enterShippingVo.setEnterReceiverName(enterShipping.getEnterReceiverName());
            enterShippingVo.setEnterReceiverTelephone(enterShipping.getEnterReceiverTelephone());
            enterShippingVo.setEnterReceiverPhone(enterShipping.getEnterReceiverPhone());
            enterShippingVo.setEnterReceiverProvince(enterShipping.getEnterReceiverProvince());
            enterShippingVo.setEnterReceiverCity(enterShipping.getEnterReceiverCity());
            enterShippingVo.setEnterReceiverDistrict(enterShipping.getEnterReceiverDistrict());
            enterShippingVo.setEnterReceiverAddress(enterShipping.getEnterReceiverAddress());
            enterShippingVo.setEnterReceiverFloor(enterShipping.getEnterReceiverFloor());
            enterShippingVo.setEnterReceiverTime(enterShipping.getEnterReceiverTime());
            enterShippingVo.setEnterTruck(enterShipping.getEnterTruck());
            enterShippingVo.setEnterAfterWork(enterShipping.getEnterAfterWork());
            if (enterShipping.getEnterStatus()==1){
                enterShippingVo.setEnterStatus(true);
            }else{
                enterShippingVo.setEnterStatus(false);
            }
            enterShippingVoList.add(enterShippingVo);
        }
        return enterShippingVoList;
    }

    public ServerResponse setDefault(Integer userId,int shippingId){
        EnterShipping enterShipping=enterShippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (enterShipping!=null){
            EnterShipping defaultShipping=enterShippingMapper.selectDefault(userId);
            if (defaultShipping!=null){
                defaultShipping.setEnterStatus(0);
                enterShippingMapper.updateStatus(defaultShipping);
            }

            enterShipping.setEnterUserId(userId);
            enterShipping.setEnterStatus(1);
            int rowCount=enterShippingMapper.updateStatus(enterShipping);
            if (rowCount > 0){
                return ServerResponse.createBySuccess("设置默认地址成功");
            }
            return ServerResponse.createByErrorMessage("设置默认地址失败");
        }else{
            return ServerResponse.createByErrorMessage("该地址不存在");
        }
    }

}
