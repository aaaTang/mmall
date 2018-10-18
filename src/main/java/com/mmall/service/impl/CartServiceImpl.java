package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.ICartService;
import com.mmall.service.IHotWordsService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import com.mmall.vo.ProductModelVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import java.math.BigDecimal;
import java.util.List;


@Service("iCartService")
@Slf4j
public class CartServiceImpl  implements ICartService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductModelMapper productModelMapper;

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

    public ServerResponse<CartVo> add(Integer userId,Integer count,Integer productId,Integer modelId){
        if (productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (modelId==null){
            List<ProductModel> productModelList=productModelMapper.selectByProductId(productId);
            if (productModelList.size()!=0){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Product product=productMapper.selectByPrimaryKey(productId);
            if (product!=null){
                Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
                if (cart==null){
                    Cart cartItem=new Cart();
                    cartItem.setModelId(0);
                    cartItem.setQuantity(count);
                    cartItem.setChecked(Const.Cart.CHECKED);
                    cartItem.setProductId(productId);
                    cartItem.setUserId(userId);

                    if (count>product.getStock()){
                        String tempFalse="库存仅有"+product.getStock()+"件，无法加入购物车";
                        return ServerResponse.createByErrorMessage(tempFalse);
                    }else {
                        cartMapper.insert(cartItem);
                    }
                }else{
                    //产品已经在购物车中，则数量相加；
                    count = cart.getQuantity()+count;
                    cart.setQuantity(count);
                    if (count>product.getStock()){
                        String tempFalse="库存仅有"+product.getStock()+"件，无法加入购物车";
                        return ServerResponse.createByErrorMessage(tempFalse);
                    }else {
                        cartMapper.updateByPrimaryKeySelective(cart);
                    }
                }
                return this.list(userId);
            }
            return ServerResponse.createByErrorMessage("添加的产品不存在或者已下架");
        }else {
            Product product=productMapper.selectByPrimaryKey(productId);
            if (product!=null){
                ProductModel productModel=productModelMapper.selectByIdProductId(modelId,productId);
                if (productModel!=null){
                    Cart cart=cartMapper.selectCartByUserIdProductIdModelId(userId,productId,modelId);

                    if (cart==null){
                        Cart cartItem=new Cart();
                        cartItem.setModelId(modelId);
                        cartItem.setQuantity(count);
                        cartItem.setChecked(Const.Cart.CHECKED);
                        cartItem.setProductId(productId);
                        cartItem.setUserId(userId);

                        if (count>productModel.getStock()){
                            String tempFalse="库存仅有"+productModel.getStock()+"件，无法加入购物车";
                            return ServerResponse.createByErrorMessage(tempFalse);
                        }
                        cartMapper.insert(cartItem);
                    }else{
                        //产品已经在购物车中，则数量相加；
                        count = cart.getQuantity()+count;
                        cart.setQuantity(count);

                        if (count>productModel.getStock()){
                            String tempFalse="库存仅有"+productModel.getStock()+"件，无法加入购物车";
                            return ServerResponse.createByErrorMessage(tempFalse);
                        }else {
                            cartMapper.updateByPrimaryKeySelective(cart);
                        }
                    }
                    return this.list(userId);
                }
                return ServerResponse.createByErrorMessage("添加的产品型号不存在或已下架");
            }
            return ServerResponse.createByErrorMessage("添加的产品不存在或者已下架");
        }
    }

    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count,Integer modelId){
        if (productId == null || count == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (modelId==null){
            List<ProductModel> productModelList=productModelMapper.selectByProductId(productId);
            if (productModelList.size()!=0){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
            if (cart!=null){
                cart.setQuantity(count);
                cartMapper.updateByPrimaryKeySelective(cart);
            }
            return this.list(userId);
        }
        Cart cart = cartMapper.selectCartByUserIdProductIdModelId(userId,productId,modelId);
        if (cart != null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds,Integer modelId){
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (modelId==0){
            cartMapper.deleteByUserIdProductIds(userId,productList);
        }else {
            int productId=Integer.valueOf(productIds);
            cartMapper.deleteByUserIdProductIdModelId(userId,productId,modelId);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer modelId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,modelId,checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId) {
        User user=userMapper.selectByPrimaryKey(userId);
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(cartList)) {
            for (int i=0;i<cartList.size();i++) {
                Cart cartItem=cartList.get(i);
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getSubImages().split(",")[0]);
                    cartProductVo.setProductName(Base64Decode(product.getName()));
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());

                    if (user.getRole()!=0){
                        BigDecimal discount=getDiscount(product.getCategoryId());
                        discount= BigDecimalUtil.sub(1,discount.doubleValue());
                        BigDecimal price=BigDecimalUtil.mul(product.getPrice().doubleValue(),discount.doubleValue());
                        cartProductVo.setProductPrice(price);

                    }else {
                        cartProductVo.setProductPrice(product.getPrice());
                    }

                    int modelId = cartItem.getModelId();
                    if (modelId == 0) {

                        //判断库存
                        int buyLimitCount = 0;
                        if (product.getStock() >= cartItem.getQuantity()) {
                            //库存充足的时候
                            buyLimitCount = cartItem.getQuantity();
                            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        } else {
                            buyLimitCount = product.getStock();
                            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                            //购物车中更新有效库存；
                            Cart cartForQuantity = new Cart();
                            cartForQuantity.setId(cartItem.getId());
                            cartForQuantity.setQuantity(buyLimitCount);

                            cartMapper.updateByPrimaryKeySelective(cartForQuantity);

                        }
                        cartProductVo.setQuantity(buyLimitCount);
                        cartProductVo.setModelExist(false);
                        cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getProductPrice().doubleValue(), cartProductVo.getQuantity()));

                        boolean temp = true;
                        if (cartItem.getChecked() == 1) {
                            cartProductVo.setProductChecked(temp);
                        } else {
                            temp = false;
                            cartProductVo.setProductChecked(temp);
                        }

                        if (cartItem.getChecked() == Const.Cart.CHECKED) {
                            //如果已经勾选，增加到整个的购物车总价中；
                            cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                        }
                        cartProductVo.setProductStock(product.getStock());
                        cartProductVoList.add(cartProductVo);
                    } else {
                        List<Cart> tempCartList = cartMapper.selectCartByUserIdProductIdList(userId, product.getId());
                        int modelNumber=tempCartList.size();
                        List<ProductModelVo> productModelVoList = Lists.newArrayList();
                        BigDecimal modelListPrice = new BigDecimal("0");
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        cartProductVo.setQuantity(0);
                        cartProductVo.setProductChecked(true);
                        cartProductVo.setModelExist(true);
                        for (Cart tempCartItem : tempCartList) {
                            ProductModel productModel = productModelMapper.selectByPrimaryKey(tempCartItem.getModelId());
                            ProductModelVo productModelVo = new ProductModelVo();
                            int buyLimitCount = 0;
                            productModelVo.setId(productModel.getId());
                            productModelVo.setName(productModel.getName());
                            productModelVo.setPrice(productModel.getPrice());
                            productModelVo.setUnit(productModel.getUnit());
                            productModelVo.setImageName(productModel.getImageName());
                            productModelVo.setChecked(true);
                            if (productModel.getStock() >= tempCartItem.getQuantity()) {
                                buyLimitCount = tempCartItem.getQuantity();
                                productModelVo.setQuantity(buyLimitCount);
                            } else {
                                buyLimitCount = productModel.getStock();
                                productModelVo.setQuantity(buyLimitCount);
                                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);

                                Cart cartForQuantity = new Cart();
                                cartForQuantity.setId(tempCartItem.getId());
                                cartForQuantity.setQuantity(buyLimitCount);

                                cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                            }

                            if (tempCartItem.getChecked()==Const.Cart.UN_CHECKED){
                                cartProductVo.setProductChecked(false);
                                productModelVo.setChecked(false);
                            }else {
                                BigDecimal productModelPrice = BigDecimalUtil.mul(productModelVo.getPrice().doubleValue(), productModelVo.getQuantity());
                                modelListPrice=BigDecimalUtil.add(modelListPrice.doubleValue(),productModelPrice.doubleValue());
                            }
                            productModelVoList.add(productModelVo);
                        }
                        cartProductVo.setProductTotalPrice(modelListPrice);
                        cartProductVo.setProductModelVoList(productModelVoList);
                        cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                        cartProductVoList.add(cartProductVo);

                        cartList.removeAll(tempCartList);
                        i=i+modelNumber-1;
                    }
                }
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllchecked(this.getAllCheckedStatus(userId));

        cartVo.setImageHost(PropertiesUtil.getProperty("little.list.prefix"));

        return cartVo;
    }

    private BigDecimal getDiscount(Integer jd_code){
        Category thirdCategory=categoryMapper.selectByJdCode(jd_code);
        Category secondCategory=categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
        Category firstCategory=categoryMapper.selectByPrimaryKey(secondCategory.getParentId());
        return firstCategory.getDiscount();
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
