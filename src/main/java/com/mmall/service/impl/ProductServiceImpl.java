package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductCollectMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ProductModelMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.pojo.ProductCollect;
import com.mmall.pojo.ProductModel;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DataTimeUtil;
import com.mmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.mmall.util.Base64Code.*;

@Slf4j
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductModelMapper productModelMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private ProductCollectMapper productCollectMapper;

    public ServerResponse saveOrUpdateProduct(Product product){

        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray=product.getSubImages().split(",");
                if (subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else {
                int rowCount =productMapper.insert(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }

                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或者更新产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){

        if (productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount=productMapper.updateByPrimaryKeySelective(product);

        if (rowCount>0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");

    }

    private ProductDetailVo assembleProductDetailVo(Product product)  {
        ProductDetailVo productDetailVo=new ProductDetailVo();

        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(Base64Decode(product.getName()));
        productDetailVo.setSubtitle(Base64Decode(product.getSubtitle()));
        productDetailVo.setMainImage(product.getMainImage());

        productDetailVo.setSmallImages(stringToArray(product.getSmallImages()));
        productDetailVo.setBigImages(stringToArray(product.getBigImages()));
        productDetailVo.setSubImages(stringToArray(product.getSubImages()));
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setSprice(product.getSprice());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setBrand(Base64Decode(product.getBrand()));
        productDetailVo.setWeight(Base64Decode(product.getWeight()));
        productDetailVo.setOriginCountry(Base64Decode(product.getOriginCountry()));
        productDetailVo.setItemDetail(Base64Decode(product.getItemDetail()));

        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //增加产品分类路径；
        ProductPathVo productPathVo=iCategoryService.getProductPath(product.getId()).getData();
        productDetailVo.setProductPathVo(productPathVo);

        List<ProductModel> productModelList=productModelMapper.selectModelList(product.getId());
        productDetailVo.setProductModelList(productModelList);

        //createTime
        productDetailVo.setCreateTime(DataTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setCreateTime(DataTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;

    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();

        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        Category category=categoryMapper.selectByJdCode(product.getCategoryId());

        productListVo.setId(product.getId());
        productListVo.setCategoryId(category.getCategoryId());
        productListVo.setCategoryName(category.getCategoryName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(Base64Decode(product.getName()));
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

    public ServerResponse<PageInfoAndBrandVo> getProductListTest(int pageNum,int pageSize,int categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectByCategoryId(category.getJdCode());
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);

        PageInfoAndBrandVo pageInfoAndBrandVo=assemblepageInfoAndBrandVo(pageInfo,null,category.getJdCode());

        return ServerResponse.createBySuccess(pageInfoAndBrandVo);
    }

    public ServerResponse<List<ProductSugVo>> getProductSugList(int categoryId) {

        List<Product> productList=productMapper.selectByCategoryId(categoryId);
        List<Product> randomProductList=getRandomList(productList);
        List<ProductSugVo> productSugVoList=Lists.newArrayList();
        for (Product product:randomProductList){
            ProductSugVo productSugVo=assembleProductSugVo(product);
            productSugVoList.add(productSugVo);
        }
        return ServerResponse.createBySuccess(productSugVoList);
    }

    private List<Product> getRandomList(List<Product> productList){
        List<Product> result=Lists.newArrayList();
        if (Const.RANDOMPRODUCTNUM<productList.size()){
            for(int i=0;i<Const.RANDOMPRODUCTNUM;i++){
                Random random = new Random();
                int randomIndex = random.nextInt(productList.size());
                Product product=productList.get(randomIndex);
                productList.remove(randomIndex);
                result.add(product);
            }
            return result;
        }else {
            return productList;
        }
    }

    private ProductSugVo assembleProductSugVo(Product product){
        ProductSugVo productSugVo=new ProductSugVo();
        productSugVo.setProductId(product.getId());
        productSugVo.setImage(product.getSubImages().split(",")[0]);
        productSugVo.setName(Base64Decode(product.getName()));
        productSugVo.setPrice(product.getPrice());
        return productSugVo;
    }

    public ServerResponse<List<ProductSugVo>> getProductLoveList(){
        List<Product> result=Lists.newArrayList();
        List<Product> productList=productMapper.selectList();
        List<ProductSugVo> productSugVoList=Lists.newArrayList();
        for(int i=0;i<Const.LOVEPRODUCTNUM;i++){
            Random random = new Random();
            int randomIndex = random.nextInt(productList.size());
            Product product=productList.get(randomIndex);
            productList.remove(randomIndex);
            result.add(product);
        }
        for(Product product:result){
            ProductSugVo productSugVo=assembleProductSugVo(product);
            productSugVoList.add(productSugVo);
        }
        return ServerResponse.createBySuccess(productSugVoList);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {

        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品不存在或者已删除");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList=new ArrayList<Integer>();

        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);

                return ServerResponse.createBySuccess(pageInfo);
            }

            categoryIdList=iCategoryService.selectCategoryAndChildrenById(category.getCategoryId()).getData();
        }

        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray=orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList =productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:Base64encode(keyword),categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList=Lists.newArrayList();

        for (Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);

            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse addCollect(Integer userId,Integer productId){
        ProductCollect productCollect;
        productCollect=productCollectMapper.selectByUserIdAndProductId(userId,productId);
        if (productCollect!=null){
            return ServerResponse.createByErrorMessage("该商品已经添加收藏");
        }
        productCollect=new ProductCollect();
        productCollect.setProductId(productId);
        productCollect.setUserId(userId);
        int rowcount=productCollectMapper.insert(productCollect);
        if (rowcount>0){
            return ServerResponse.createBySuccess("插入成功");
        }
        return ServerResponse.createByErrorMessage("插入失败");
    }

    public ServerResponse delectCollect(Integer userId,Integer productId){

        int rowcount=productCollectMapper.deleteByUserIdProductId(userId,productId);
        if (rowcount>0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    public ServerResponse<PageInfo> getCollect(Integer role,Integer userId,int pageNum,int pageSize){

        List<Product> productList=Lists.newArrayList();

        PageHelper.startPage(pageNum,pageSize);
        List<ProductCollect> productCollectList=productCollectMapper.selectByUserId(userId);
        for (ProductCollect productCollect:productCollectList){
            Product product;
            product=productMapper.selectByPrimaryKey(productCollect.getProductId());
            productList.add(product);
        }

        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<PageInfoAndBrandVo> getProductListByKeyword(int pageNum, int pageSize, String keyword) {

        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(Base64encode(keyword)).append("%").toString();

        }
        List<Product> productList=productMapper.selectByKeyword("%"+keyword+"%");
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);

        PageInfoAndBrandVo pageInfoAndBrandVo=assemblepageInfoAndBrandVo(pageInfo,keyword,null);

        return ServerResponse.createBySuccess(pageInfoAndBrandVo);

    }

    private PageInfoAndBrandVo assemblepageInfoAndBrandVo(PageInfo pageInfo,String keyword,Integer categoryId){

        PageInfoAndBrandVo pageInfoAndBrandVo=new PageInfoAndBrandVo();
        pageInfoAndBrandVo.setPageInfo(pageInfo);

        if (categoryId!=null){
            List<String> brandList=productMapper.selectBrandByCategory(categoryId);
            for (int i=0;i<brandList.size();i++){
                brandList.set(i,Base64Decode(brandList.get(i)));
            }
            pageInfoAndBrandVo.setBrandList(brandList);
        }else{
            List<String> brandList=productMapper.selectBrandByKeyword(keyword);
            for (int i=0;i<brandList.size();i++){
                brandList.set(i,Base64Decode(brandList.get(i)));
            }
            pageInfoAndBrandVo.setBrandList(brandList);
        }
        return pageInfoAndBrandVo;

    }

    public ServerResponse<PageInfo> getProduct(Integer zb,Integer categoryId,int pageNum,int pageSize){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList;
        if (zb==1) {
            productList = productMapper.selectByCategoryId(category.getJdCode());
        }else {
            productList = productMapper.selectByCategoryIdZb(category.getJdCode());
        }
        List<ProductManageVo> productManageVoList=Lists.newArrayList();
        for(Product product :productList){
            ProductManageVo productManageVo=assemblePManageVo(product);
            productManageVoList.add(productManageVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productManageVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductManageVo assemblePManageVo(Product product){
        ProductManageVo productManageVo=new ProductManageVo();

        productManageVo.setId(product.getId());
        productManageVo.setCategoryId(product.getCategoryId());
        productManageVo.setName(Base64Decode(product.getName()));
        productManageVo.setMainImage(product.getMainImage());
        productManageVo.setSprice(product.getSprice());
        productManageVo.setPrice(product.getPrice());
        productManageVo.setStock(product.getStock());
        productManageVo.setBrand(Base64Decode(product.getBrand()));
        productManageVo.setStatus(product.getStatus());

        return productManageVo;

    }

    public ServerResponse updateProduct(Integer id, String name, BigDecimal sprice, BigDecimal price, String brand, Integer status){
        Product product=productMapper.selectByPrimaryKey(id);
        if (product==null){
            return ServerResponse.createByErrorMessage("该产品不存在；");
        }

        Product newProduct=new Product();
        newProduct.setId(id);
        newProduct.setName(Base64encode(name));
        newProduct.setSprice(sprice);
        newProduct.setPrice(price);
        newProduct.setBrand(Base64encode(brand));
        newProduct.setStatus(status);

        int rowCount=productMapper.updateByPrimaryKeySelective(newProduct);
        if (rowCount==0){
            return ServerResponse.createByErrorMessage("更新失败！");
        }
        return ServerResponse.createBySuccess("更新成功！");
    }

    public ServerResponse changeCategory(Integer productId,Integer categoryId){
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("该产品id无对应产品");
        }
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        Product newProduct=new Product();
        newProduct.setId(productId);
        newProduct.setCategoryId(category.getJdCode());
        int rowCount=productMapper.updateByPrimaryKeySelective(newProduct);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新成功！");
        }
        return ServerResponse.createByErrorMessage("更新失败！");
    }

    public ServerResponse getAllProduct(){
        List<testVo> categoryVoList=Lists.newArrayList();
        List<Category>  categoryList=categoryMapper.getAllSecondCategory();
        for (Category category:categoryList){
            testVo testvo=new testVo();
            testvo.setName(category.getCategoryName());
            testvo.setCode(category.getCategoryCode());
            testvo.setBrandNum(productMapper.selectBrandNum(category.getJdCode()).size());
            testvo.setSkuNum(productMapper.selectProductNumByCategoryId(category.getJdCode()));
            categoryVoList.add(testvo);
        }
        return ServerResponse.createBySuccess(categoryVoList);

    }

    public ServerResponse<PageInfo> getProductByCategoryIdAndBrand(int pageNum,int pageSize,int categoryId,String brand){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);

        PageHelper.startPage(pageNum,pageSize);

        List<Product> productList=productMapper.selectByCategoryIdAndBrand(category.getJdCode(),Base64encode(brand));
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<PageInfo> getProductByKeywordIdAndBrand(int pageNum,int pageSize,String keyword,String brand){

        PageHelper.startPage(pageNum,pageSize);

        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(Base64encode(keyword)).append("%").toString();

        }

        List<Product> productList=productMapper.selectByKeywordAndBrand(keyword,Base64encode(brand));
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse selectNull(){
        List<Category> categoryhas=categoryMapper.getSecondCategoryHas();
        List<Category> categoryNull=Lists.newArrayList();

        for (Category category:categoryhas){
            List<Product> productList=productMapper.selectByCategoryId(category.getJdCode());
            if(productList==null||productList.isEmpty()){
                categoryNull.add(category);
            }
        }
        return ServerResponse.createBySuccess(categoryNull);
    }

    public ServerResponse getProductModel(Integer prdouctId,Integer pageNum,Integer pageSize){
        if (prdouctId==null){
            return ServerResponse.createByErrorMessage("请传入productId");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<ProductModel> productModelList=productModelMapper.selectModelList(prdouctId);
        List<ProductModelListVo> productModelListVoList=Lists.newArrayList();
        for (ProductModel productModel:productModelList){
            ProductModelListVo productModelListVo=assembleProductModelListVo(productModel);
            productModelListVoList.add(productModelListVo);
        }
        PageInfo pageInfo=new PageInfo(productModelList);
        pageInfo.setList(productModelListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse saveProductModel(ProductModel productModel){
        if (productModel.getId()==null){
            int rowCount=productModelMapper.insertSelective(productModel);
            if (rowCount>0){
                return ServerResponse.createBySuccess("产品规格添加成功");
            }
            return ServerResponse.createByErrorMessage("产品规格添加失败，请检查是否有必要参数未传入");
        }
        ProductModel newProductModel=productModelMapper.selectByPrimaryKey(productModel.getId());
        if (newProductModel==null){
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        int rowCount=productModelMapper.updateByPrimaryKeySelective(productModel);
        if (rowCount>0){
            return ServerResponse.createBySuccess("产品规格更新成功");
        }
        return ServerResponse.createByErrorMessage("产品规格更新失败");
    }

    public ServerResponse deleteProductModel(Integer productModelId){
        if (productModelId==null){
            return ServerResponse.createByErrorMessage("请传入productModelId");
        }
        int rowCount=productModelMapper.deleteByPrimaryKey(productModelId);
        if (rowCount>0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    private ProductModelListVo assembleProductModelListVo(ProductModel productModel){
        ProductModelListVo productModelListVo=new ProductModelListVo();

        productModelListVo.setId(productModel.getId());
        productModelListVo.setProductId(productModel.getProductId());
        productModelListVo.setName(productModel.getName());
        productModelListVo.setPrice(productModel.getPrice());
        productModelListVo.setUnit(productModel.getUnit());
        productModelListVo.setStock(productModel.getStock());
        productModelListVo.setImageName(productModel.getImageName());
        productModelListVo.setStatus(productModel.getStatus());
        productModelListVo.setOrderby(productModel.getOrderby());

        return productModelListVo;
    }

}
