package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.HotProductMapper;
import com.mmall.pojo.HotProduct;
import com.mmall.service.IHotProductService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.HotProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iHotProductService")
public class HotProductServiceImpl implements IHotProductService {

    @Autowired
    private HotProductMapper hotProductMapper;

    public ServerResponse<HotProductListVo> getList(){
        List<HotProduct> hotProductList=hotProductMapper.getList();
        HotProductListVo hotProductListVo=assembleHotProductList(hotProductList);
        return ServerResponse.createBySuccess(hotProductListVo);
    }

    private HotProductListVo assembleHotProductList(List<HotProduct> hotProductList){
        HotProductListVo hotProductListVo=new HotProductListVo();
        hotProductListVo.setHotProductList(hotProductList);
        hotProductListVo.setImageHost("");
        //hotProductListVo.setImageHost(PropertiesUtil.getProperty("little.list.prefix","http://image.99sbl.com/"));
        return hotProductListVo;
    }

    public ServerResponse saveOrUpdateProduct(HotProduct hotProduct){
        if (hotProduct!=null){
            if (StringUtils.isNotBlank(hotProduct.getSubImages())){
                String[] subImageArray=hotProduct.getSubImages().split(",");
                if (subImageArray.length>0){
                    hotProduct.setMainImage(subImageArray[0]);
                }
            }

            if (hotProduct.getId()!=null){
                int rowCount = hotProductMapper.updateByPrimaryKey(hotProduct);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else {
                int rowCount =hotProductMapper.insert(hotProduct);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或者更新产品参数不正确");
    }
}
