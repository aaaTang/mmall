package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.FloorCategoryMapper;
import com.mmall.dao.FloorProductMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.FloorCategory;
import com.mmall.pojo.FloorProduct;
import com.mmall.pojo.Product;
import com.mmall.service.IFloorService;
import com.mmall.vo.FloorProductListVo;
import com.mmall.vo.FloorProductVo;
import com.mmall.vo.FloorVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.mmall.util.Base64Code.*;

@Service("iFloorService")
@Slf4j
public class FloorServiceImpl implements IFloorService{

    @Autowired
    private FloorCategoryMapper floorCategoryMapper;

    @Autowired
    private FloorProductMapper floorProductMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse createFloor(FloorCategory floorCategory){
        int rowCount=floorCategoryMapper.insert(floorCategory);
        if (rowCount>0){
            return ServerResponse.createBySuccess("新建楼层成功");
        }
        return ServerResponse.createByErrorMessage("新建失败");
    }

    public ServerResponse updateFloor(FloorCategory floorCategory){
        int rowCount=floorCategoryMapper.updateByPrimaryKeySelective(floorCategory);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新楼层成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    public ServerResponse deleteFloor(Integer floorId){
        int rowCount=floorCategoryMapper.deleteByPrimaryKey(floorId);
        if (rowCount>0){
            return ServerResponse.createBySuccess("删除楼层成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    public ServerResponse listFloorSort(){
        List<FloorCategory> floorCategoryList=floorCategoryMapper.selectFloorSort();

        List<FloorVo> floorVoList=Lists.newArrayList();

        for (FloorCategory floorCategory:floorCategoryList){
            FloorVo floorVo=assembleFloorVo(floorCategory);
            floorVoList.add(floorVo);
        }
        return ServerResponse.createBySuccess(floorVoList);
    }

    public ServerResponse addProduct(Integer floorId,Integer categoryId,Integer productId){
        FloorCategory floorCategory=floorCategoryMapper.selectByPrimaryKey(floorId);
        if (floorCategory==null){
            return ServerResponse.createByErrorMessage("该楼层id不存在,请重新选择");
        }
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.createByErrorMessage("该分类不存在");
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("该产品id不存在,请重新选择");
        }

        FloorProduct floorProduct=new FloorProduct();
        floorProduct.setFloorId(floorId);
        floorProduct.setCategoryId(categoryId);
        floorProduct.setProductId(productId);

        int rowCount=floorProductMapper.insert(floorProduct);
        if (rowCount>0){
            return ServerResponse.createBySuccess("新建产品成功");
        }
        return ServerResponse.createByErrorMessage("新建产品失败");
    }

    public ServerResponse deleteProduct(Integer floorProductId){
        int rowcount=floorProductMapper.deleteByPrimaryKey(floorProductId);
        if (rowcount>0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    public ServerResponse productList(){
        List<FloorProduct> floorProductList=floorProductMapper.selectAllFloorProduct();

        List<FloorProductVo> floorProductVoList=Lists.newArrayList();
        for (FloorProduct floorProduct:floorProductList){
            FloorProductVo floorProductVo=assembleFloorProductVo(floorProduct);
            floorProductVoList.add(floorProductVo);
        }
        return ServerResponse.createBySuccess(floorProductVoList);
    }

    private FloorProductVo assembleFloorProductVo(FloorProduct floorProduct){
        FloorProductVo floorProductVo=new FloorProductVo();

        FloorCategory floorCategory=floorCategoryMapper.selectByPrimaryKey(floorProduct.getFloorId());
        Category category=categoryMapper.selectByPrimaryKey(floorProduct.getCategoryId());
        Product product=productMapper.selectByPrimaryKey(floorProduct.getProductId());

        floorProductVo.setId(floorProduct.getId());
        floorProductVo.setFloorId(floorProduct.getFloorId());
        floorProductVo.setFloorName(floorCategory.getFloorName());
        floorProductVo.setProductId(floorProduct.getProductId());
        floorProductVo.setCategoryId(floorProduct.getCategoryId());
        floorProductVo.setCategoryName(category.getCategoryName());
        floorProductVo.setName(Base64Decode(product.getName()));
        floorProductVo.setMainImage(product.getMainImage());
        floorProductVo.setPrice(product.getPrice());
        floorProductVo.setStatus(product.getStatus());

        return floorProductVo;
    }

    private FloorVo assembleFloorVo(FloorCategory floorCategory){
        FloorVo floorVo=new FloorVo();

        List<FloorProduct> firstFloorProductList=floorProductMapper.selectFloorProduct(floorCategory.getId(),floorCategory.getFirstCategoryid());
        List<FloorProduct> secondFloorProductList=floorProductMapper.selectFloorProduct(floorCategory.getId(),floorCategory.getSecondCategoryid());
        List<FloorProduct> thirdFloorProductList=floorProductMapper.selectFloorProduct(floorCategory.getId(),floorCategory.getThirdCategoryid());

        List<FloorProductListVo> firstProductListVoList=assembleProductListVoList(firstFloorProductList);
        List<FloorProductListVo> secondProductListVoList=assembleProductListVoList(secondFloorProductList);
        List<FloorProductListVo> thirdProductListVoList=assembleProductListVoList(thirdFloorProductList);

        floorVo.setId(floorCategory.getId());
        floorVo.setFloorName(floorCategory.getFloorName());
        floorVo.setFloorImage(floorCategory.getFloorImage());
        floorVo.setFirstCategoryname(categoryMapper.selectByPrimaryKey(floorCategory.getFirstCategoryid()).getCategoryName());
        floorVo.setFirstCategoryid(floorCategory.getFirstCategoryid());
        floorVo.setSecondCategoryname(categoryMapper.selectByPrimaryKey(floorCategory.getSecondCategoryid()).getCategoryName());
        floorVo.setSecondCategoryid(floorCategory.getSecondCategoryid());
        floorVo.setThirdCategoryname(categoryMapper.selectByPrimaryKey(floorCategory.getThirdCategoryid()).getCategoryName());
        floorVo.setThirdCategoryid(floorCategory.getThirdCategoryid());
        floorVo.setMoreCategoryname(categoryMapper.selectByPrimaryKey(floorCategory.getMoreCategoryid()).getCategoryName());
        floorVo.setMoreCategoryid(floorCategory.getMoreCategoryid());
        floorVo.setSortOrder(floorCategory.getSortOrder());

        floorVo.setFirstProductList(firstProductListVoList);
        floorVo.setSecondProductList(secondProductListVoList);
        floorVo.setThirdProductList(thirdProductListVoList);

        return floorVo;
    }

    private List<FloorProductListVo> assembleProductListVoList(List<FloorProduct> floorProductList){
        List<FloorProductListVo> productListVoList= Lists.newArrayList();

        for (FloorProduct floorProduct:floorProductList){
            Product product=productMapper.selectByPrimaryKey(floorProduct.getProductId());

            Category category=categoryMapper.selectByJdCode(product.getCategoryId());

            FloorProductListVo floorProductListVo=new FloorProductListVo();
            floorProductListVo.setFloorProductId(floorProduct.getId());
            floorProductListVo.setId(product.getId());
            floorProductListVo.setCategoryId(category.getCategoryId());
            floorProductListVo.setCategoryName(category.getCategoryName());
            floorProductListVo.setName(Base64Decode(product.getName()));
            floorProductListVo.setMainImage(product.getMainImage());
            floorProductListVo.setPrice(product.getPrice());
            floorProductListVo.setStatus(product.getStatus());

            productListVoList.add(floorProductListVo);

        }
        return productListVoList;
    }

}
