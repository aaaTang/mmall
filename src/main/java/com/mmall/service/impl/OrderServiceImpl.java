package com.mmall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.AlipayConfig;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IMessageService;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private UserLevelMapper userLevelMapper;

    @Autowired
    private CheckOrderMapper checkOrderMapper;

    @Autowired
    private EnterShippingMapper enterShippingMapper;

    @Autowired
    private ProductModelMapper productModelMapper;

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private DrawbackMapper drawbackMapper;

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

    public ServerResponse createOrder(Integer userId,Integer shippingId){
        //从购物车中获取数据
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);

        //计算订单总价；
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.issuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment=this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order =this.assembleOrder(userId,shippingId,payment);
        if (order==null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);

        //生成成功，要减少库存
        this.reductProductStock(orderItemList);

        //清空购物车
        this.cleanCart(cartList);

        //返回给前端数据；
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        String address=null;
        if (orderVo.getShippingVo()!=null){
            address=orderVo.getShippingVo().getReceiverProvince()+orderVo.getShippingVo().getReceiverCity()+orderVo.getShippingVo().getReceiverDistrict()+orderVo.getShippingVo().getReceiverAddress();
        }else {
            address=orderVo.getEnterShippingVo().getEnterReceiverProvince()+orderVo.getEnterShippingVo().getEnterReceiverCity()+orderVo.getEnterShippingVo().getEnterReceiverDistrict()+orderVo.getEnterShippingVo().getEnterReceiverAddress();
        }
        //生成消息提醒客户付款；
        String userName="系统管理员";
        String content="感谢您的购物，您的订单已经生成，订单号："+order.getOrderNo()+",订单金额："+order.getPayment()+",收货人："
                +orderVo.getReceiverName()+",送货地址："+address+"。目前等待您的付款，付款后您可以进入会员中心处理订单，将订单状态置为“已付款”，或者通知我们您已付款，我们将在收到款项之后立即安排发货。";
        String title="您的订单："+order.getOrderNo()+",处于待付款状态！";
        String messageUrl="对应订单号的付款链接";
        iMessageService.add(userName,title,content,messageUrl,order.getUserId());
        return ServerResponse.createBySuccess(orderVo);

    }

    public ServerResponse quickCreateOrder(Integer userId,Integer productId,Integer modelId,Integer count,Integer shippingId){

        ServerResponse serverResponse = this.getQuickOrderItem(userId,productId,modelId,count);
        if (!serverResponse.issuccess()){
            return serverResponse;
        }
        OrderItem orderItem = (OrderItem) serverResponse.getData();
        BigDecimal payment=orderItem.getTotalPrice();

        Order order =this.assembleOrder(userId,shippingId,payment);
        if (order==null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        orderItem.setOrderNo(order.getOrderNo());
        orderItemMapper.insert(orderItem);
        List<OrderItem> orderItemList=Lists.newArrayList();
        orderItemList.add(orderItem);
        this.reductProductStock(orderItemList);

        //返回给前端数据；
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        String address=null;
        if (orderVo.getShippingVo()!=null){
            address=orderVo.getShippingVo().getReceiverProvince()+orderVo.getShippingVo().getReceiverCity()+orderVo.getShippingVo().getReceiverDistrict()+orderVo.getShippingVo().getReceiverAddress();
        }else {
            address=orderVo.getEnterShippingVo().getEnterReceiverProvince()+orderVo.getEnterShippingVo().getEnterReceiverCity()+orderVo.getEnterShippingVo().getEnterReceiverDistrict()+orderVo.getEnterShippingVo().getEnterReceiverAddress();
        }
        //生成消息提醒客户付款；
        String userName="系统管理员";
        String content="感谢您的购物，您的订单已经生成，订单号："+order.getOrderNo()+",订单金额："+order.getPayment()+",收货人："
                +orderVo.getReceiverName()+",送货地址："+address+"。目前等待您的付款，付款后您可以进入会员中心处理订单，将订单状态置为“已付款”，或者通知我们您已付款，我们将在收到款项之后立即安排发货。";
        String title="您的订单："+order.getOrderNo()+",处于待付款状态！";
        String messageUrl="对应订单号的付款链接";
        iMessageService.add(userName,title,content,messageUrl,order.getUserId());
        return ServerResponse.createBySuccess(orderVo);

    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setCurUserId(0);
        orderVo.setCurUserName("test");
        orderVo.setStatus(order.getStatus());
        orderVo.setExpressType(order.getExpressType());
        if (order.getExpressType()==null){
            orderVo.setExpressTypeDesc(null);
        }else{
            orderVo.setExpressTypeDesc(Const.ExpressTypeEnum.codeOf(order.getExpressType()).getValue());
        }
        orderVo.setExpressNub(order.getExpressNub());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        EnterShipping enterShipping=enterShippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        if (enterShipping != null){
            orderVo.setReceiverName(enterShipping.getEnterReceiverName());
            orderVo.setEnterShippingVo(assembleEnterShippingVo(enterShipping));
        }

        orderVo.setPaymentTime(DataTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DataTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DataTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DataTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DataTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("little.list.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for (OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){

        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderItemId(orderItem.getId());
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());

        orderItemVo.setModelName(orderItem.getModelName());
        orderItemVo.setModelPrice(orderItem.getModelPrice());
        orderItemVo.setModelUnit(orderItem.getModelUnit());

        if (orderItem.getModelStatus()==1){
            orderItemVo.setModelStatus(true);
        }else {
            orderItemVo.setModelStatus(false);
        }

        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DataTimeUtil.dateToStr(orderItem.getCreateTime()));

        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVo;
    }

    private EnterShippingVo assembleEnterShippingVo(EnterShipping enterShipping){
        EnterShippingVo enterShippingVo=new EnterShippingVo();
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
        if (enterShipping.getEnterStatus()==1){
            enterShippingVo.setEnterStatus(true);
        }else{
            enterShippingVo.setEnterStatus(false);
        }
        enterShippingVo.setEnterAfterWork(enterShipping.getEnterAfterWork());

        return enterShippingVo;
    }

    private void cleanCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }

    }

    private void reductProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()- orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);

        order.setStatus(Const.OrderStatusEnum.DISDEAL.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.UN_DEAL.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间等等
        //付款时间等等
        int rowCount = orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;
    }

    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList){
            log.info(String.valueOf(orderItem.getTotalPrice()));
            payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServerResponse getQuickOrderItem(Integer userId,Integer productId,Integer modelId,Integer count){
        OrderItem orderItem=new OrderItem();
        User user=userMapper.selectByPrimaryKey(userId);
        Product product = productMapper.selectByPrimaryKey(productId);
        if (Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
            return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"不是在线售卖状态");
        }

        if (modelId==0){
            if (count>product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"库存不足");
            }
            orderItem.setModelStatus(0);
        }else {
            ProductModel productModel=productModelMapper.selectByPrimaryKey(modelId);
            if (count>productModel.getStock()){
                return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+" 型号："+productModel.getName()+"库存不足");
            }
            orderItem.setModelStatus(1);
            orderItem.setModelName(productModel.getName());
            orderItem.setModelPrice(productModel.getPrice());
            orderItem.setModelUnit(productModel.getUnit());
            orderItem.setTotalPrice(BigDecimalUtil.mul(productModel.getPrice().doubleValue(),count));
        }

        orderItem.setUserId(userId);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(Base64Decode(product.getName()));
        orderItem.setProductImage(product.getSubImages().split(",")[0]);

        if (user.getRole()!=0){
            BigDecimal discount=getDiscount(product.getCategoryId());
            discount= BigDecimalUtil.sub(1,discount.doubleValue());
            BigDecimal price=BigDecimalUtil.mul(product.getPrice().doubleValue(),discount.doubleValue());
            orderItem.setCurrentUnitPrice(price);

        }else {
            orderItem.setCurrentUnitPrice(product.getPrice());
        }

        orderItem.setQuantity(count);
        orderItem.setTotalPrice(BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),count));

        return ServerResponse.createBySuccess(orderItem);
    }

    private BigDecimal getDiscount(Integer jd_code){
        Category thirdCategory=categoryMapper.selectByJdCode(jd_code);
        Category secondCategory=categoryMapper.selectByPrimaryKey(thirdCategory.getParentId());
        Category firstCategory=categoryMapper.selectByPrimaryKey(secondCategory.getParentId());
        return firstCategory.getDiscount();
    }

    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList=Lists.newArrayList();
        User user=userMapper.selectByPrimaryKey(userId);
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数据，包括产品的状态和数量；
        for (Cart cartItem:cartList){
            OrderItem orderItem=new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
                return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"不是在线售卖状态");
            }
            BigDecimal price;
            if (cartItem.getModelId()==0){
                if (cartItem.getQuantity()>product.getStock()){
                    return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"库存不足");
                }
                orderItem.setModelStatus(0);

                if (user.getRole()!=0){
                    BigDecimal discount=getDiscount(product.getCategoryId());
                    discount= BigDecimalUtil.sub(1,discount.doubleValue());
                    price=BigDecimalUtil.mul(product.getPrice().doubleValue(),discount.doubleValue());
                }else {
                    price=product.getPrice();
                }

                orderItem.setTotalPrice(BigDecimalUtil.mul(price.doubleValue(),cartItem.getQuantity()));
            }else {
                ProductModel productModel=productModelMapper.selectByPrimaryKey(cartItem.getModelId());
                if (cartItem.getQuantity()>productModel.getStock()){
                    return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+" 型号："+productModel.getName()+"库存不足");
                }
                orderItem.setModelStatus(1);
                orderItem.setModelName(productModel.getName());
                price=productModel.getPrice();
                orderItem.setModelPrice(productModel.getPrice());
                orderItem.setModelUnit(productModel.getUnit());
                orderItem.setTotalPrice(BigDecimalUtil.mul(productModel.getPrice().doubleValue(),cartItem.getQuantity()));
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(Base64Decode(product.getName()));
            orderItem.setProductImage(product.getSubImages().split(",")[0]);
            orderItem.setCurrentUnitPrice(price);
            orderItem.setQuantity(cartItem.getQuantity());

            orderItemList.add(orderItem);

        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    public ServerResponse<String> cancel(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("该用户订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.UNSUBMIT.getCode()){
            return ServerResponse.createByErrorMessage("此订单无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();

    }

    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据

        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse=this.getCartOrderItem(userId,cartList);
        if (!serverResponse.issuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList= (List<OrderItem>)serverResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order !=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    public OrderVo getOrderVO(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return orderVo;
    }

    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList=assembleOrderVoList(orderList,userId);

        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);

    }

    public ServerResponse submit(Integer userId, Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        Order newOrder=new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatusEnum.ONCHECK.getCode());
        newOrder.setPaymentType(3);
        orderMapper.updateByPrimaryKeySelective(newOrder);

        CheckOrder checkOrder=assembleCheckOrder(order);
        if (checkOrderMapper.selectByOrderNo(orderNo)!=null){
            return ServerResponse.createByErrorMessage("该订单已提交，请勿重复提交");
        }
        checkOrderMapper.insert(checkOrder);
        int role=userMapper.selectByPrimaryKey(userId).getRole();
        if (role==4){
            order.setStatus(Const.OrderStatusEnum.SUCCESSCHECK.getCode());
        }else{
            order.setStatus(Const.OrderStatusEnum.ONCHECK.getCode());
        }
        return ServerResponse.createBySuccess("提交审核成功");
    }

    public ServerResponse<PageInfo> getCheckList(Integer userId,int pageNum,int pageSize){
        User user=userMapper.selectByPrimaryKey(userId);
        List<CheckOrder> checkOrderList;
        if (user.getRole()==1){
            checkOrderList=checkOrderMapper.selectCheckOrderListByStartUser(userId);
        }else if(user.getRole()==4){
            checkOrderList=checkOrderMapper.selectCheckOrderList4(userId);
        }
        else {
            checkOrderList=checkOrderMapper.selectCheckOrderList(userId);
        }

//        if (order !=null){
//            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
//            OrderVo orderVo = assembleOrderVo(order,orderItemList);
//            return ServerResponse.createBySuccess(orderVo);
//        }
        PageHelper.startPage(pageNum,pageSize);
        List<CheckOrderVo> checkOrderVoList=assembleCheckOrderVo(checkOrderList);
        PageInfo pageResult=new PageInfo(checkOrderVoList);
        pageResult.setList(checkOrderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private List<CheckOrderVo> assembleCheckOrderVo(List<CheckOrder> checkOrderList){
        List<CheckOrderVo> checkOrderVoList=Lists.newArrayList();

        for (CheckOrder checkOrder:checkOrderList){
            Order order=orderMapper.selectByOrderNo(checkOrder.getOrderNo());
            CheckOrderVo checkOrderVo=new CheckOrderVo();
            checkOrderVo.setId(checkOrder.getId());
            checkOrderVo.setOrderNo(checkOrder.getOrderNo());
            checkOrderVo.setPayment(order.getPayment());
            checkOrderVo.setStartUser(checkOrder.getStartUser());
            checkOrderVo.setStartUserName(userMapper.selectByPrimaryKey(checkOrder.getStartUser()).getUsername());
            checkOrderVo.setCurUser(checkOrder.getCurUser());
            checkOrderVo.setCurUserName(userMapper.selectByPrimaryKey(checkOrder.getCurUser()).getUsername());
            checkOrderVo.setCurLv(checkOrder.getCurLv());
            checkOrderVo.setStatus(checkOrder.getStatus());
            checkOrderVo.setStatusDesc(Const.CheckStatusEnum.codeOf(checkOrder.getStatus()).getValue());
            checkOrderVo.setLvId(checkOrder.getLvId());

            List<OrderItemVo> orderItemVoList = Lists.newArrayList();
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(checkOrder.getOrderNo());

            for(OrderItem orderItem:orderItemList){
                OrderItemVo orderItemVo=assembleOrderItemVo(orderItem);
                orderItemVoList.add(orderItemVo);
            }

            checkOrderVo.setOrderItemVoList(orderItemVoList);
            checkOrderVo.setCreateTime(DataTimeUtil.dateToStr(checkOrder.getCreateTime()));
            checkOrderVoList.add(checkOrderVo);

        }
        return checkOrderVoList;
    }

    public ServerResponse fourCheckOrder(int userId,int checkOrderId,int lvId,int status){
        CheckOrder checkOrder=checkOrderMapper.selectByPrimaryKey(checkOrderId);
        Order order=orderMapper.selectByOrderNo(checkOrder.getOrderNo());
        if (checkOrder!=null){
            if (status==1){
                checkOrder.setStatus(status);
                checkOrderMapper.updateByPrimaryKey(checkOrder);
                order.setStatus(Const.OrderStatusEnum.FAILCHECK.getCode());
                orderMapper.updateByPrimaryKey(order);
                return ServerResponse.createBySuccess("点击成功");
            }
            if (status==2){
                UserLevel userLevel=userLevelMapper.selectByPrimaryKey(lvId);
                switch (checkOrder.getCurLv()){
                    case 2:
                        checkOrder.setCurUser(userLevel.getLv3());
                        checkOrder.setCurLv(3);
                        checkOrderMapper.updateByPrimaryKey(checkOrder);
                        return ServerResponse.createBySuccess("点击成功");
                    case 3:
                        checkOrder.setCurUser(userLevel.getLv4());
                        checkOrder.setCurLv(4);
                        checkOrderMapper.updateByPrimaryKey(checkOrder);
                        return ServerResponse.createBySuccess("点击成功");
                    case 4:
                        checkOrder.setStatus(2);
                        checkOrderMapper.updateByPrimaryKey(checkOrder);
                        order.setStatus(Const.OrderStatusEnum.SUCCESSCHECK.getCode());
                        orderMapper.updateByPrimaryKey(order);
                        return ServerResponse.createBySuccess("点击成功");
                }
            }
            return ServerResponse.createByErrorMessage("审核状态错误");

        }
        return ServerResponse.createByErrorMessage("此待审核订单不存在");
    }

    public ServerResponse threeCheckOrder(int userId,int checkOrderId,int lvId,int status){
        CheckOrder checkOrder=checkOrderMapper.selectByPrimaryKey(checkOrderId);
        Order order=orderMapper.selectByOrderNo(checkOrder.getOrderNo());
        if (checkOrder!=null){
            if (status==1){
                checkOrder.setStatus(status);
                checkOrderMapper.updateByPrimaryKey(checkOrder);
                order.setStatus(Const.OrderStatusEnum.FAILCHECK.getCode());
                orderMapper.updateByPrimaryKey(order);
                return ServerResponse.createBySuccess("点击成功");
            }
            if (status==2){
                UserLevel userLevel=userLevelMapper.selectByPrimaryKey(lvId);
                switch (checkOrder.getCurLv()){
                    case 2:
                        checkOrder.setCurUser(userLevel.getLv3());
                        checkOrder.setCurLv(3);
                        checkOrderMapper.updateByPrimaryKey(checkOrder);
                        return ServerResponse.createBySuccess("点击成功");
                    case 3:
                        checkOrder.setStatus(2);
                        checkOrderMapper.updateByPrimaryKey(checkOrder);
                        order.setStatus(Const.OrderStatusEnum.SUCCESSCHECK.getCode());
                        orderMapper.updateByPrimaryKey(order);
                        return ServerResponse.createBySuccess("点击成功");
                }
            }
            return ServerResponse.createByErrorMessage("审核状态错误");
        }
        return ServerResponse.createByErrorMessage("此待审核订单不存在");
    }

    private CheckOrder assembleCheckOrder(Order order){
        CheckOrder checkOrder=new CheckOrder();
        checkOrder.setOrderNo(order.getOrderNo());
        checkOrder.setStartUser(order.getUserId());
        User user=userMapper.selectByPrimaryKey(order.getUserId());
        int userRole=user.getRole();
        UserLevel userLeve;
        switch (userRole){
            case -1:
                userLeve=userLevelMapper.selectBylv1(order.getUserId());
                checkOrder.setLvId(userLeve.getId());
                checkOrder.setCurUser(userLeve.getLv2());
                checkOrder.setCurLv(2);
                checkOrder.setStatus(0);
                return checkOrder;
            case 2:
                userLeve=userLevelMapper.selectBylv2(order.getUserId());
                checkOrder.setLvId(userLeve.getId());
                checkOrder.setCurUser(userLeve.getLv3());
                checkOrder.setCurLv(3);
                checkOrder.setStatus(0);
                return checkOrder;
            case 3:
                userLeve=userLevelMapper.selectBylv3(order.getUserId());
                checkOrder.setLvId(userLeve.getId());
                checkOrder.setCurUser(userLeve.getLv4());
                checkOrder.setCurLv(4);
                checkOrder.setStatus(0);
                return checkOrder;
            case 4:
                userLeve=userLevelMapper.selectBylv4(order.getUserId());
                checkOrder.setLvId(userLeve.getId());
                checkOrder.setCurUser(userLeve.getLv4());
                checkOrder.setCurLv(4);
                checkOrder.setStatus(2);
                return checkOrder;
            default:
                return null;
        }
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order:orderList){
            List<OrderItem> orderItemList=Lists.newArrayList();
            if (userId==null){
                orderItemList=orderItemMapper.getByOrderNo(order.getOrderNo());
            }
            else{
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    public ServerResponse confirmOrder(Integer userId, Long orderNo) {
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus()==Const.OrderStatusEnum.EXPRESSON.getCode()){
            order.setStatus(Const.OrderStatusEnum.DELIVERY.getCode());
            orderMapper.updateByPrimaryKey(order);
            return ServerResponse.createBySuccess("确认收货成功");
        }
        return ServerResponse.createByErrorMessage("该订单暂未发货或其他情况");
    }

    public ServerResponse delete(Integer userId, Long orderNo) {
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus()==Const.OrderStatusEnum.CANCELED.getCode()||order.getStatus()==Const.OrderStatusEnum.UNSUBMIT.getCode()) {
            order.setStatus(Const.OrderStatusEnum.DELETE.getCode());
            orderMapper.updateByPrimaryKey(order);
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("此订单无法删除");
    }

    public ServerResponse setPay(Long orderNo, Integer userId){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        Order newOrder=new Order();
        newOrder.setId(order.getId());
        newOrder.setOrderNo(orderNo);
        newOrder.setStatus(Const.OrderStatusEnum.UNDELIVERY.getCode());
        newOrder.setPaymentType(2);
        orderMapper.updateByPrimaryKeySelective(newOrder);
        order.setStatus(20);
        return ServerResponse.createBySuccess("线下付款成功");
    }

    public ServerResponse pay(Long orderNo, Integer userId){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }

        Order newOrder=new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatusEnum.DISPAY.getCode());
        newOrder.setOrderNo(orderNo);
        newOrder.setPaymentType(1);
        orderMapper.updateByPrimaryKeySelective(newOrder);

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        alipayRequest.setReturnUrl(AlipayConfig.return_url);

        String outTradeNo = order.getOrderNo().toString();
        String totalAmount = order.getPayment().toString();
        String subject = new StringBuilder().append("思贝丽商城扫码支付,订单号:").append(outTradeNo).toString();
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ outTradeNo +"\","
                + "\"total_amount\":\""+ totalAmount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"2m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        try {
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            log.info(result);
            return ServerResponse.createBySuccess(result);
        } catch (AlipayApiException e) {
            return ServerResponse.createByErrorMessage(e.getMessage());
        }

    }

    public ServerResponse<List<OrderVo>> selectOrder(int userId, int year, int month, String startTime, String endTime){
        if (year!=-1 && month==-1){
            String tempStartTime=String.valueOf(year)+"-01-01 00:00:00";
            String tempEndTime=String.valueOf(year)+"-12-31 23:59:59";
            List<Order> orderList=orderMapper.selectByYear(userId,tempStartTime,tempEndTime);
            List<OrderVo> orderVoList=assembleOrderVoList(orderList,userId);
            return ServerResponse.createBySuccess(orderVoList);
        }

        if (year!=-1 && month!=-1){

            String maxDay=getDaysByYearMonth(year,month);
            String tempStartTime=String.valueOf(year)+"-"+getMonth(month)+"-01 00:00:00";
            String tempEndTime=String.valueOf(year)+"-"+getMonth(month)+"-"+maxDay+" 23:59:59";
            log.info(tempStartTime);
            log.info(tempEndTime);
            List<Order> orderList=orderMapper.selectByYear(userId,tempStartTime,tempEndTime);
            List<OrderVo> orderVoList=assembleOrderVoList(orderList,userId);
            return ServerResponse.createBySuccess(orderVoList);
        }

        if (startTime!="-1"&endTime!="-1"){
            List<Order> orderList=orderMapper.selectByYear(userId,startTime+" 00:00:00",endTime+" 23:59:59");
            List<OrderVo> orderVoList=assembleOrderVoList(orderList,userId);
            return ServerResponse.createBySuccess(orderVoList);
        }

        return ServerResponse.createByErrorMessage("参数错误");
    }

    public String getMonth(int month){
        if (month<10){
            return "0"+String.valueOf(month);
        }
        else {
            return String.valueOf(month);
        }
    }

    public String getDaysByYearMonth(int year,int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month-1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return String.valueOf(maxDate);
    }

    //backend

    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectAllOrder();
        List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);

    }

    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    public ServerResponse<String> manageSendGoods(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            if (order.getStatus()==Const.OrderStatusEnum.SUCCESSCHECK.getCode()){
                order.setStatus(Const.OrderStatusEnum.EXPRESSON.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            }else if (order.getStatus()==Const.OrderStatusEnum.FAILCHECK.getCode()){
                return ServerResponse.createByErrorMessage("订单审核未通过");
            }
            return ServerResponse.createByErrorMessage("订单号不存在或已取消");
        } else{
            return ServerResponse.createByErrorMessage("订单号为空");
        }
    }

    public ServerResponse<String> query(String orderNo){
        String app_id="2018070360524249";
        String private_key="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDO71C2oMvFYTB8yo0VhSZv6O0o6jAHzzmlOeeya7sp26HqS8nD8vgZo8GpRmgsZRWRSqCbW1eP3I0Uq58YI16gP8Xk5nYybyI/aiLck7PD/LnwAxh5HdsyqB+KoLLYBDQh81d9HZU3EhwDf7bd43Yo1qhpIuz+tG3teYWQCjiFkeCt6VuGHYnrcfZJxIJGhW6ZL1o5WOKGGPDlacrk0ICXuT24fJYZY0PAwZp75WqIoEPuUFsxXo69+3pNnT+IoAvDeWfZ/bbEAD6yDjUV2pdQ2ris+xRa+rihub9IqFXF/XQoDjW0TyV8qGDTMmYPWtSXmeNhAnorqgDXlRhBYf1RAgMBAAECggEAJAlvH+7OrbfoLsNDYI0IjZKdwnNOG/4NhuWXoO278WUrRFcgcvxcEnL/JdB6EckkwWGiqIt2qzn4Y7IjiZuXPgb9Goi76rqJ10tPeORL+QSJCPAxEd6OLrsyivzDSHUq8wKiqMo/ExEXSdCy8t9K03/WYkDPzudzAVkZVmVBRJk7n8xcoGZpaoAfdogGASOaoGITcETn8qlAaSXvnjpDMUyJCuI6Ujbj+wRdifUx6NBm+Do9rN8KvEYDOhBeBQWabmQVlU92IO21qKcV5TWx2vAkaA/FhEL/tYHKzJUV7HPKom2uG+4IuGRoXb1vdLNOkPwtWLjgdrGivF39L27aMQKBgQDz3I8SzNcmfOxnGP1fy8Vkk1pSDjb+HSzXbCllCQ7EvWaLUUAlqTJXU2T11nbZ8HGkvEYfLK0IzhqWLW0Dci7MsRbEC6Hup4OWMYByIHDSuVeErJVNXW28toB/Clwm5VeetfOXA8ggsCLaaJBZrjBI7ZOFkgZGMdb5ZJKZHgRCRQKBgQDZPDZRf9oTSg9Z4X9Si2cEe5OsWjqVWhGaDx/yM+lb8xesptIXYQX28v7TeVbhkY7gUdd/qpiiU7j51ji2KyG+B2e7cGVZhRWTnHaOWjPu4bZ+fScA7YYTe2VjLogPtZwkTn/nfEiOtwH/Z1cUvjxk8UUkY3hxFhtnI1H0qjIFnQKBgHqnjeLT2sdGABWe8Rn0wPTkVIJ/GdPYUsyLX4qs8pur897Q4CXRIzve+yHXW/IkSNSlydM2QlXybFbqxDD+hmF4FM5IV4tVAA9UGJnOjVC/3jAiKfo+qspHNPww9sathdOTCtEDGu6uCOm3vBsPTMPxksiU+MjnmovXtRCEk3nNAoGAS8EJmCYjoDJlZAyU3+4pAJEvkBzu7QI5vSWlGEsuB5igGt8ZyHlXISTf5FMeDw85adRgd+6/x2u2x7kCkdQ8sg3XCLBLR4p3WcBoP4AJODaR804leddauBSB6LJjVEcaqjaNt+XJT6tWEQCyh1NHsFPKrsPZTmZNLAxPGij3xKkCgYA54mWxUm8ASLXgkHpbgVDbjL39rXlxNQ5ZHUFCttuOasTD5k2qpkdhhv7BElO0VURDjp8qBKlmExzrnf3PZmFaLL8+0gI5KlQlQ09cmk+Rs0fz2z5rKwyAx7mWNP9st5GiCcSnR5iULEhl1oYlL5cR9mqeLn1mPC6SEYPlHiQhZg==";
        String public_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwTBLGo4+YYADjATOkLuGZqlz8CWeOEMVIZ/ne6zpRojCP8O/urOhilGYigpFKzcYkY7q2qD/xacGCvM165CNIpqTTqL28ZKqJhS9OU06pGjczGmmP52VtNjZsN//5ss1j0KqPXToMqegRqUcyOc+i3jxBn3XGfZKBYG903LZroV2Y6kQpq4MlyxOL2YhDRbvRIAF60FzQgbItyGzYpupF2aDATV2PO9aSom5ZG4Li6neeq/YGZ/h6R8tZqCFyYKjrPbdArH+priR7zw+gM+v7UuCpkbqpmzfewLDQJB6r2k/UBl/vx/D/ji6BT+AUuIwuNsQX1OXiJHwiNd88pJdtwIDAQAB";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",app_id,private_key,"json","GBK",public_key,"RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"trade_no\":\""+orderNo+"\"" +
                "  }");
        try{
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                String content=response.getBody();
                return ServerResponse.createBySuccess(content);

            } else {
                return ServerResponse.createByErrorMessage("调用失败");
            }
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("调用失败");
        }
    }

    public void setPayStatus(String orderNo){
        Long orderNoLong=Long.valueOf(orderNo);
        Order order=orderMapper.selectByOrderNo(orderNoLong);

        Order newOrder=new Order();

        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatusEnum.YESPAY.getCode());

        System.out.println(newOrder.getId());
        System.out.println(newOrder.getStatus());

        orderMapper.updateByPrimaryKeySelective(newOrder);
    }

    public void ceshi(){
        Long orderNoLong=Long.valueOf("1539448030009");
        Order order=orderMapper.selectByOrderNo(orderNoLong);
        System.out.println(order);
    }

    public ServerResponse drawback(int userId, Long orderNo, int type, BigDecimal money, int reason, int refund_way, String description) {
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order!=null){

            Drawback testDrawBack=drawbackMapper.selectByUserIdAndOrderNo(userId,orderNo);
            if (testDrawBack!=null){
                return ServerResponse.createByErrorMessage("您的订单："+orderNo+" 已申请售后，请勿重复申请！");
            }
            int compareIndex=money.compareTo(order.getPayment());
            if (compareIndex==1){
                return ServerResponse.createByErrorMessage("您申请的售后金额大于订单金额，请重新输入！");
            }
            Drawback drawback=new Drawback();
            drawback.setOrderTime(order.getCreateTime());
            drawback.setUserId(userId);
            drawback.setOrderNo(orderNo);
            drawback.setServiceType(type);
            drawback.setDrawbackMoney(money);
            drawback.setReason(reason);
            drawback.setRefundWay(refund_way);
            drawback.setDescription(description);

            drawbackMapper.insert(drawback);

            Order newOrder=new Order();
            newOrder.setId(order.getId());
            newOrder.setStatus(Const.OrderStatusEnum.DRAWBACK.getCode());

            orderMapper.updateByPrimaryKeySelective(newOrder);

            return ServerResponse.createBySuccess("创建成功");
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }
}
