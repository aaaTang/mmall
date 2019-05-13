package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.vo.ProductPathVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){

        Set<Category> categorySet= Sets.newHashSet();

        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId!=null){
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getCategoryId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    public ServerResponse<List<Category>> getList(){
        List<Category> categoryList=categoryMapper.getList();
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<Category>> getEnterList(){
        List<Category> categoryList=categoryMapper.getAllList();
        return ServerResponse.createBySuccess(categoryList);
    }

//    //递归算法
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){

        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个推出的条件
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem:categoryList){
            findChildCategory(categorySet ,categoryItem.getCategoryId());
        }
        return categorySet;
    }

    public ServerResponse<ProductPathVo> getProductPath(int productId) {
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品id不存在");
        }

        Category thirdCategory=categoryMapper.selectByJdCode(product.getCategoryId());
        Category secondCategory=categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
        Category firstCategory=categoryMapper.selectByPrimaryKey(secondCategory.getParentId());

        ProductPathVo productPathVo=new ProductPathVo();

        productPathVo.setFirstCategoryid(firstCategory.getCategoryId());
        productPathVo.setFirstCategoryCode(firstCategory.getCategoryCode());
        productPathVo.setFirstCategoryName(firstCategory.getCategoryName());
        productPathVo.setSecondCategoryId(secondCategory.getCategoryId());
        productPathVo.setSecondCategoryCode(secondCategory.getCategoryCode());
        productPathVo.setSecondCategoryName(secondCategory.getCategoryName());
        productPathVo.setThirdCategoryId(thirdCategory.getCategoryId());
        productPathVo.setThirdCategoryCode(thirdCategory.getCategoryCode());
        productPathVo.setThirdCategoryName(thirdCategory.getCategoryName());

        return ServerResponse.createBySuccess(productPathVo);
    }

    public ServerResponse saveCategory(Category category){
        if (category.getCategoryId()==null){
            if (category.getParentId()==null||category.getCategoryName()==null||category.getLevel()==null||category.getStatus()==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            category.setJdCode(generatorJdcode());
            int rowCount=categoryMapper.insertSelective(category);
            if (rowCount>0){
                return ServerResponse.createBySuccess("目录添加成功");
            }
            return ServerResponse.createByErrorMessage("添加失败，请联系技术人员！");
        }
        Category selectCategory=categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if (selectCategory==null){
            return ServerResponse.createByErrorMessage("该分类id不存在，无法更新！");
        }
        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新分类成功");
        }
        return ServerResponse.createByErrorMessage("更新失败，请联系技术人员！");
    }

    public ServerResponse deleteCategory(Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.createByErrorMessage("该分类id错误，请检查该参数");
        }
        int rowCount=categoryMapper.deleteByPrimaryKey(categoryId);
        if(rowCount>0){
            return ServerResponse.createBySuccess("删除该分类成功");
        }
        return ServerResponse.createByErrorMessage("删除失败，请联系技术人员！");
    }

    private int generatorJdcode(){
        int code=new Random().nextInt(10000000);
        Category category=categoryMapper.selectByJdCode(code);
        if (category==null)
            return code;
        else
            return generatorJdcode();
    }

}
