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
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DataTimeUtil;
import com.mmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

//    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
//
//        if (productId==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
//        }
//        Product product=productMapper.selectByPrimaryKey(productId);
//        if (product==null){
//            return ServerResponse.createByErrorMessage("产品已下架或者删除");
//        }
//
//        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
//        return ServerResponse.createBySuccess(productDetailVo);
//    }

    public String[] stringToArray(String subImage){
        String[] array=subImage.split(",");
        return array;
    }

    public String Base64Decode(String encodeStr) {
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            byte[] b = decoder.decodeBuffer(encodeStr);
            String str = new String(b,"utf-8");
            return str;
        }catch (Exception e){
            return null;
        }
    }

    public String Base64encode(String decodeStr) {
        BASE64Encoder encoder = new BASE64Encoder();
        try{
            byte[] textByte = decodeStr.getBytes("UTF-8");
            String encodedText = encoder.encode(textByte);
            return encodedText;
        }catch (Exception e){
            return null;
        }
    }

    private ProductDetailVo assembleProductDetailVo(Integer role,Product product)  {
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

        if (role!=0){
            BigDecimal discount=getDiscount(product.getCategoryId());
            discount= BigDecimalUtil.sub(1,discount.doubleValue());
            BigDecimal price=BigDecimalUtil.mul(product.getPrice().doubleValue(),discount.doubleValue());
            productDetailVo.setPrice(price);

        }else {
            productDetailVo.setPrice(product.getPrice());
        }
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

    private BigDecimal getDiscount(Integer jd_code){
        Category thirdCategory=categoryMapper.selectByJdCode(jd_code);
        Category secondCategory=categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
        Category firstCategory=categoryMapper.selectByPrimaryKey(secondCategory.getParentId());
        return firstCategory.getDiscount();
    }

    public ServerResponse<PageInfo> getProductList(Integer role,int pageNum,int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();

        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(role,productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Integer role,Product product){
        ProductListVo productListVo=new ProductListVo();
        Category category=categoryMapper.selectByJdCode(product.getCategoryId());

        productListVo.setId(product.getId());
        productListVo.setCategoryId(category.getCategoryId());
        productListVo.setCategoryName(category.getCategoryName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(Base64Decode(product.getName()));

        if (role!=0){
            BigDecimal discount=getDiscount(product.getCategoryId());
            discount= BigDecimalUtil.sub(1,discount.doubleValue());
            BigDecimal price=BigDecimalUtil.mul(product.getPrice().doubleValue(),discount.doubleValue());
            productListVo.setPrice(price);

        }else {
            productListVo.setPrice(product.getPrice());
        }

        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

    private ProductListTestVo assembleProductListTestVo(Category category,List<ProductListVo> productListVo){
        ProductListTestVo productListTestVo=new ProductListTestVo();

        productListTestVo.setName(category.getCategoryName());
        productListTestVo.setId(category.getCategoryId());
        productListTestVo.setParentId(category.getParentId());
        productListTestVo.setChildren(productListVo);

        return productListTestVo;
    }

    public ServerResponse<PageInfo> getProductListTest(Integer role,int pageNum,int pageSize,int categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);

        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectByCategoryId(category.getJdCode());
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(role,productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
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

//    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize){
//
//        PageHelper.startPage(pageNum,pageSize);
//        if (StringUtils.isNotBlank(productName)){
//            productName=new StringBuilder().append("%").append(productName).append("%").toString();
//        }
//        List<Product> productList=productMapper.selectByNameAndProductId(productName,productId);
//        List<ProductListVo> productListVoList=Lists.newArrayList();
//        for (Product productItem:productList){
//            ProductListVo productListVo=assembleProductListVo(productItem);
//            productListVoList.add(productListVo);
//        }
//        PageInfo pageResult=new PageInfo(productList);
//        pageResult.setList(productListVoList);
//        return ServerResponse.createBySuccess(pageResult);
//    }

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

    public ServerResponse<ProductDetailVo> getProductDetail(Integer role,Integer productId) {

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
        ProductDetailVo productDetailVo=assembleProductDetailVo(role,product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(Integer role,String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
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

//        if (StringUtils.isNotBlank(keyword)){
//            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
//
//        }
        //排序处理


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
            ProductListVo productListVo=assembleProductListVo(role,product);

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
            ProductListVo productListVo=assembleProductListVo(role,productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse queryCollect(Integer userId,Integer product){
        ProductCollect productCollect=productCollectMapper.selectByUserIdAndProductId(userId,product);
        if (productCollect!=null){
            return ServerResponse.createBySuccess(1);
        }
        return ServerResponse.createBySuccess(0);
    }


    public ServerResponse getProductListByKeyword(Integer role,int pageNum, int pageSize, String keyword) {
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(Base64encode(keyword)).append("%").toString();

        }
        List<Product> productList=productMapper.selectByKeyword("%"+keyword+"%");
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(role,productItem);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
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







































}
