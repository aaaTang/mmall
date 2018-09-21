package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.InvoiceShippingMapper;
import com.mmall.pojo.InvoiceShipping;
import com.mmall.service.IInvoiceShippingService;
import com.mmall.vo.InvoiceShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iInvoiceShippingService")
public class InvoiceShippingServiceImpl implements IInvoiceShippingService {

    @Autowired
    private InvoiceShippingMapper invoiceShippingMapper;

    public ServerResponse add(Integer userId, InvoiceShipping invoiceShipping, String status){
        invoiceShipping.setUserId(userId);
        if (status.equals("true")){
            invoiceShipping.setShippingStatus(1);
            InvoiceShipping defaultInvoiceShipping=invoiceShippingMapper.selectDefault(userId);
            if (defaultInvoiceShipping!=null){
                defaultInvoiceShipping.setShippingStatus(0);
                invoiceShippingMapper.updateStatus(defaultInvoiceShipping);
            }
        }else {
            invoiceShipping.setShippingStatus(0);
        }
        int rowCount=invoiceShippingMapper.insert(invoiceShipping);
        if (rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",invoiceShippingMapper.selectMaxId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        int resultCount=invoiceShippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId,InvoiceShipping invoiceShipping, String status){
        invoiceShipping.setUserId(userId);
        if (status.equals("true")){
            invoiceShipping.setShippingStatus(1);
            InvoiceShipping defaultInvoiceShipping=invoiceShippingMapper.selectDefault(userId);
            if (defaultInvoiceShipping!=null){
                defaultInvoiceShipping.setShippingStatus(0);
                invoiceShippingMapper.updateStatus(defaultInvoiceShipping);
            }
        }else {
            invoiceShipping.setShippingStatus(0);
        }
        int rowCount=invoiceShippingMapper.updateByShipping(invoiceShipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<InvoiceShippingVo> select(Integer userId,Integer shippingId){

        InvoiceShipping invoiceShipping=invoiceShippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (invoiceShipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        InvoiceShippingVo invoiceShippingVo=assembleInvoiceShippingVo(invoiceShipping);
        return  ServerResponse.createBySuccess("查询地址成功",invoiceShippingVo);

    }

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<InvoiceShipping> invoiceShippingList=invoiceShippingMapper.selectByUserId(userId);
        List<InvoiceShippingVo> shippingVoList=assembleInvoiceShippingVoList(invoiceShippingList);
        PageInfo pageInfo=new PageInfo(shippingVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private InvoiceShippingVo assembleInvoiceShippingVo(InvoiceShipping invoiceShipping){
        InvoiceShippingVo invoiceShippingVo=new InvoiceShippingVo();

        invoiceShippingVo.setInvoiceShippingId(invoiceShipping.getInvoiceShippingId());
        invoiceShippingVo.setUserId(invoiceShipping.getUserId());
        invoiceShippingVo.setReceiverName(invoiceShipping.getReceiverName());
        invoiceShippingVo.setReceiverPhone(invoiceShipping.getReceiverPhone());
        invoiceShippingVo.setReceiverTelephone(invoiceShipping.getReceiverTelephone());
        invoiceShippingVo.setReceiverProvince(invoiceShipping.getReceiverProvince());
        invoiceShippingVo.setReceiverCity(invoiceShipping.getReceiverCity());
        invoiceShippingVo.setReceiverDistrict(invoiceShipping.getReceiverDistrict());
        invoiceShippingVo.setReceiverAddress(invoiceShipping.getReceiverAddress());
        invoiceShippingVo.setReceiverZip(invoiceShipping.getReceiverZip());
        if (invoiceShipping.getShippingStatus()==1){
            invoiceShippingVo.setShippingStatus(true);
        }else{
            invoiceShippingVo.setShippingStatus(false);
        }

        return invoiceShippingVo;
    }

    private List<InvoiceShippingVo> assembleInvoiceShippingVoList(List<InvoiceShipping> invoiceShippingList){
        List<InvoiceShippingVo> invoiceShippingVoList=Lists.newArrayList();
        for (InvoiceShipping invoiceShipping:invoiceShippingList){
            InvoiceShippingVo invoiceShippingVo=new InvoiceShippingVo();
            invoiceShippingVo.setInvoiceShippingId(invoiceShipping.getInvoiceShippingId());
            invoiceShippingVo.setUserId(invoiceShipping.getUserId());
            invoiceShippingVo.setReceiverName(invoiceShipping.getReceiverName());
            invoiceShippingVo.setReceiverPhone(invoiceShipping.getReceiverPhone());
            invoiceShippingVo.setReceiverTelephone(invoiceShipping.getReceiverTelephone());
            invoiceShippingVo.setReceiverProvince(invoiceShipping.getReceiverProvince());
            invoiceShippingVo.setReceiverCity(invoiceShipping.getReceiverCity());
            invoiceShippingVo.setReceiverDistrict(invoiceShipping.getReceiverDistrict());
            invoiceShippingVo.setReceiverAddress(invoiceShipping.getReceiverAddress());
            invoiceShippingVo.setReceiverZip(invoiceShipping.getReceiverZip());
            if (invoiceShipping.getShippingStatus()==1){
                invoiceShippingVo.setShippingStatus(true);
            }else{
                invoiceShippingVo.setShippingStatus(false);
            }
            invoiceShippingVoList.add(invoiceShippingVo);
        }
        return invoiceShippingVoList;
    }

    public ServerResponse setDefault(Integer userId,int shippingId){
        InvoiceShipping invoiceShipping=invoiceShippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (invoiceShipping!=null){
            InvoiceShipping defaultInvoiceShipping=invoiceShippingMapper.selectDefault(userId);
            if (defaultInvoiceShipping!=null){
                defaultInvoiceShipping.setShippingStatus(0);
                invoiceShippingMapper.updateStatus(defaultInvoiceShipping);
            }

            invoiceShipping.setUserId(userId);
            invoiceShipping.setShippingStatus(1);
            int rowCount=invoiceShippingMapper.updateStatus(invoiceShipping);
            if (rowCount > 0){
                return ServerResponse.createBySuccess("设置默认地址成功");
            }
            return ServerResponse.createByErrorMessage("设置默认地址失败");
        }else{
            return ServerResponse.createByErrorMessage("该地址不存在");
        }
    }

}
