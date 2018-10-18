package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.HotProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.HotProduct;
import com.mmall.service.IHotProductService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.HotProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iHotProductService")
public class HotProductServiceImpl implements IHotProductService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private HotProductMapper hotProductMapper;

    public ServerResponse<HotProductListVo> getList(Integer role){
        List<HotProduct> hotProductList=hotProductMapper.getList();
        HotProductListVo hotProductListVo=assembleHotProductList(role,hotProductList);
        return ServerResponse.createBySuccess(hotProductListVo);
    }

    private HotProductListVo assembleHotProductList(Integer role,List<HotProduct> hotProductList){
        HotProductListVo hotProductListVo=new HotProductListVo();
        hotProductListVo.setHotProductList(changePrice(role,hotProductList));
        hotProductListVo.setImageHost("");
        //hotProductListVo.setImageHost(PropertiesUtil.getProperty("little.list.prefix","http://image.99sbl.com/"));
        return hotProductListVo;
    }

    private BigDecimal getDiscount(Integer jd_code){
        Category thirdCategory=categoryMapper.selectByJdCode(jd_code);
        Category secondCategory=categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
        Category firstCategory=categoryMapper.selectByPrimaryKey(secondCategory.getParentId());
        return firstCategory.getDiscount();
    }

    private List<HotProduct> changePrice(Integer role,List<HotProduct> hotProductList){
        if (role==0){
            return hotProductList;
        }else {
            for (HotProduct hotProduct:hotProductList){

                BigDecimal discount=getDiscount(hotProduct.getCategoryId());
                discount= BigDecimalUtil.sub(1,discount.doubleValue());
                BigDecimal price=BigDecimalUtil.mul(hotProduct.getPrice().doubleValue(),discount.doubleValue());
                hotProduct.setPrice(price);
            }
            return hotProductList;
        }
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
