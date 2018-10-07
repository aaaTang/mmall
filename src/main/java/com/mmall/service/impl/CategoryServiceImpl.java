package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.vo.ProductPathVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

//    public ServerResponse addCategory(String categoryName,Integer parentId){
//
//        if (parentId==null||StringUtils.isBlank(categoryName)){
//            return ServerResponse.createByErrorMessage("添加品类参数错误");
//        }
//
//        Category category=new Category();
//        category.setName(categoryName);
//        category.setParentId(parentId);
//        category.setStatus(true);//这个分类是可以用的
//
//        int rowCount=categoryMapper.insert(category);
//        if (rowCount>0){
//            return ServerResponse.createBySuccess("添加品类成功");
//        }
//
//        return ServerResponse.createByErrorMessage("添加品类失败");
//    }
//
//    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
//        if (categoryId==null||StringUtils.isBlank(categoryName)){
//            return ServerResponse.createByErrorMessage("更新品类参数错误");
//        }
//        Category category=new Category();
//        category.setId(categoryId);
//        category.setName(categoryName);
//
//        int rowCount= categoryMapper.updateByPrimaryKeySelective(category);
//
//        if (rowCount>0){
//            return ServerResponse.createBySuccess("更新品类名字成功");
//        }
//        return ServerResponse.createByErrorMessage("更新品类名字失败");
//
//    }
//
//    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
//
//        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        if (CollectionUtils.isEmpty(categoryList)){
//            log.info("未找到当前分类的子分类");
//        }
//        return ServerResponse.createBySuccess(categoryList);
//    }
//
//    /**
//     * 递归查询本节点的id及孩子节点的id
//     * @param categoryId
//     * @return
//     */
//    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
//
//        Set<Category> categorySet= Sets.newHashSet();
//
//        findChildCategory(categorySet,categoryId);
//
//        List<Integer> categoryIdList= Lists.newArrayList();
//        if(categoryId!=null){
//            for (Category categoryItem:categorySet){
//                categoryIdList.add(categoryItem.getId());
//            }
//        }
//        return ServerResponse.createBySuccess(categoryIdList);
//    }

    public ServerResponse<List<Category>> getList(){
        List<Category> categoryList=categoryMapper.getList();
        return ServerResponse.createBySuccess(categoryList);
    }

//    //递归算法
//    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
//
//        Category category=categoryMapper.selectByPrimaryKey(categoryId);
//        if (category!=null){
//            categorySet.add(category);
//        }
//        //查找子节点，递归算法一定要有一个推出的条件
//        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        for (Category categoryItem:categoryList){
//            findChildCategory(categorySet ,categoryItem.getId());
//        }
//        return categorySet;
//    }

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
}
