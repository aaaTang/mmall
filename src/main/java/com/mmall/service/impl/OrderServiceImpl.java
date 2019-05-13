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
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IMessageService;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.SendMailUtil;
import com.mmall.util.dzfp.FileUtil;
import com.mmall.util.dzfp.Gzip;
import com.mmall.util.dzfp.HttpRequestUtils;
import com.mmall.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.mail.internet.MimeUtility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

import static com.mmall.util.Base64Code.Base64Decode;

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

    @Autowired
    private InvoiceMapper invoiceMapper;

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
                +orderVo.getReceiverName()+",送货地址："+address+",请您确认订单信息，并选择付款方式。";
        String title="您的订单："+order.getOrderNo()+"已生成，请选择付款方式！";
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
                +orderVo.getReceiverName()+",送货地址："+address+",请您确认订单信息，并选择付款方式。";
        String title="您的订单："+order.getOrderNo()+"已生成，请选择付款方式！";
        String messageUrl="对应订单号的付款链接";
        iMessageService.add(userName,title,content,messageUrl,order.getUserId());
        return ServerResponse.createBySuccess(orderVo);

    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());

        if (order.getPaymentType()==Const.PaymentTypeEnum.OFFLINE_PAY.getCode()){
            CheckOrder checkOrder=checkOrderMapper.selectByOrderNo(order.getOrderNo());
            CheckDetailVo checkDetailVo=new CheckDetailVo();
            checkDetailVo.setCurrentLv(checkOrder.getCurLv());
            checkDetailVo.setCurrentName(userMapper.selectByPrimaryKey(checkOrder.getCurUser()).getUsername());
            checkDetailVo.setCheckOption(checkOrder.getCheckOption());

            orderVo.setCheckDetailVo(checkDetailVo);
        }

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
        orderItemVo.setModelName(orderItem.getModelName());

        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());

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

    public long generateOrderNo(){
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

        Product product = productMapper.selectByPrimaryKey(productId);
        if (Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
            return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"不是在线售卖状态");
        }

        if (modelId==0){
            if (count>product.getStock()){
                return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+"库存不足");
            }
            orderItem.setModelStatus(0);
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),count));
        }else {
            ProductModel productModel=productModelMapper.selectByPrimaryKey(modelId);
            if (count>productModel.getStock()){
                return ServerResponse.createByErrorMessage("产品"+Base64Decode(product.getName())+" 型号："+productModel.getName()+"库存不足");
            }
            orderItem.setModelStatus(1);
            orderItem.setModelName(productModel.getName());
            orderItem.setCurrentUnitPrice(productModel.getPrice());
            orderItem.setModelPrice(productModel.getPrice());
            orderItem.setModelUnit(productModel.getUnit());
            orderItem.setTotalPrice(BigDecimalUtil.mul(productModel.getPrice().doubleValue(),count));
        }
        orderItem.setUserId(userId);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(Base64Decode(product.getName()));
        orderItem.setProductImage(product.getSubImages().split(",")[0]);

        orderItem.setQuantity(count);

        return ServerResponse.createBySuccess(orderItem);
    }

    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList=Lists.newArrayList();

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
                price=product.getPrice();
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

    public ServerResponse fourCheckOrder(int userId,int checkOrderId,int lvId,int status,String checkOption){
        CheckOrder checkOrder=checkOrderMapper.selectByPrimaryKey(checkOrderId);
        Order order=orderMapper.selectByOrderNo(checkOrder.getOrderNo());
        if (checkOrder!=null){
            if (status==1){
                checkOrder.setStatus(status);
                checkOrder.setCheckOption(checkOption);
                checkOrderMapper.updateByPrimaryKey(checkOrder);
                order.setStatus(Const.OrderStatusEnum.FAILCHECK.getCode());
                orderMapper.updateByPrimaryKey(order);

                String userName="系统管理员";
                String content="感谢您的购物，您的订单号："+order.getOrderNo()+",订单金额："+order.getPayment()+"对应的订单由"+String.valueOf(checkOrder.getCurLv())+"级审核员："+userMapper.selectByPrimaryKey(checkOrder.getCurUser()).getUsername()+"审核不通过，审核意见为："+checkOption+"，请您重新修改订单或者重新下单。";
                String title="您的订单："+order.getOrderNo()+"审核不通过";
                String messageUrl="http://www.99sbl.com/#/orderDetail/"+order.getOrderNo();
                iMessageService.add(userName,title,content,messageUrl,order.getUserId());

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
        if (order.getStatus()==Const.OrderStatusEnum.CANCELED.getCode()||order.getStatus()==Const.OrderStatusEnum.UNSUBMIT.getCode()||order.getStatus()==Const.OrderStatusEnum.DISDEAL.getCode()) {
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

    public ServerResponse<PageInfo> manageSearch(Integer status,String name,Long orderNo,String phone,int pageNum,int pageSize){
        if (name!=null&name!=""){
            Shipping shipping=shippingMapper.selectByName(name);
            if (shipping==null){
                return ServerResponse.createByErrorMessage("该姓名没有订单");
            }
            PageHelper.startPage(pageNum,pageSize);
            List<Order>  orderList=orderMapper.selectByUSerIdAndShippingid(shipping.getUserId(),shipping.getId(),status);
            if (orderList==null){
                return ServerResponse.createByErrorMessage("该姓名没有订单");
            }
            List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);

            PageInfo pageResult = new PageInfo(orderList);
            pageResult.setList(orderVoList);

            return ServerResponse.createBySuccess(pageResult);
        }

        if (phone!=null&phone!=""){
            Shipping shipping=shippingMapper.selectByPhone(phone);
            if (shipping==null){
                return ServerResponse.createByErrorMessage("该手机号码没有订单");
            }
            PageHelper.startPage(pageNum,pageSize);
            List<Order>  orderList=orderMapper.selectByUSerIdAndShippingid(shipping.getUserId(),shipping.getId(),status);
            if (orderList==null){
                return ServerResponse.createByErrorMessage("该手机号码没有订单");
            }
            List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);

            PageInfo pageResult = new PageInfo(orderList);
            pageResult.setList(orderVoList);

            return ServerResponse.createBySuccess(pageResult);
        }

        if(orderNo!=null){
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

        if (status!=null){
            PageHelper.startPage(pageNum,pageSize);
            List<Order> orderList=orderMapper.selectByStatus(status);
            List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);
            PageInfo pageResult = new PageInfo(orderList);
            pageResult.setList(orderVoList);

            return ServerResponse.createBySuccess(pageResult);
        }

        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectAllOrder();
        List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);

    }

    public ServerResponse<String> manageSendGoods(Long orderNo,Integer type,String num){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            if (order.getStatus()==Const.OrderStatusEnum.UNDELIVERY.getCode()||order.getStatus()==Const.OrderStatusEnum.SUCCESSCHECK.getCode()){
                order.setStatus(Const.OrderStatusEnum.EXPRESSON.getCode());
                order.setExpressType(type);
                order.setExpressNub(num);
                order.setSendTime(new Date());
                int rowCount=orderMapper.updateByPrimaryKeySelective(order);
                if (rowCount>0){
                    String receiverName=null;
                    String address=null;

                    Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
                    if (shipping!=null){
                        receiverName=shipping.getReceiverName();
                        address=shipping.getReceiverProvince()+shipping.getReceiverCity()+shipping.getReceiverDistrict()+shipping.getReceiverAddress();
                    }else{
                        EnterShipping enterShipping=enterShippingMapper.selectByPrimaryKey(order.getShippingId());
                        receiverName=enterShipping.getEnterReceiverName();
                        address=enterShipping.getEnterReceiverProvince()+enterShipping.getEnterReceiverCity()+enterShipping.getEnterReceiverDistrict()+enterShipping.getEnterReceiverAddress();
                    }

                    String userName="系统管理员";
                    String content="感谢您的购物，您的订单已经发货，订单号："+order.getOrderNo()+",订单金额："+order.getPayment()+",收货人："
                            +receiverName+",送货地址："+address+"。";
                    String title="您的订单："+order.getOrderNo()+"已发货，您可以在订单详情中查看物流信息";
                    String messageUrl="http://www.99sbl.com/#/orderDetail/"+orderNo;
                    iMessageService.add(userName,title,content,messageUrl,order.getUserId());

                    return ServerResponse.createBySuccess("发货成功");
                }
                return ServerResponse.createByErrorMessage("发货失败，请联系技术人员！");
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

    public ServerResponse<OrderNumVo> getOrderNum(){

        OrderNumVo orderNumVo=new OrderNumVo();

        int undelivery=orderMapper.selectNumByStatus(Const.OrderStatusEnum.UNDELIVERY.getCode());
        int expresson=orderMapper.selectNumByStatus(Const.OrderStatusEnum.EXPRESSON.getCode());
        int drawback=orderMapper.selectNumByStatus(Const.OrderStatusEnum.DRAWBACK.getCode());

        orderNumVo.setUndelivery(undelivery);
        orderNumVo.setExpresson(expresson);
        orderNumVo.setDrawback(drawback);

        return ServerResponse.createBySuccess(orderNumVo);
    }

    public ServerResponse<PageInfo> getDrawback(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Drawback> drawbackList=drawbackMapper.selectAllDrawback();
        List<DrawbackVo> drawbackVoList=Lists.newArrayList();
        for(Drawback drawback:drawbackList){
            DrawbackVo drawbackVo=assembleDrawbackVo(drawback);
            drawbackVoList.add(drawbackVo);
        }
        PageInfo pageResult=new PageInfo(drawbackList);
        pageResult.setList(drawbackVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse changePayment(Long orderNo, BigDecimal payment){
        if (orderNo==null||payment==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("该订单号不存在！");
        }
        if (payment.compareTo(new BigDecimal("0"))<=0){
            return ServerResponse.createByErrorMessage("修改后价格小于或等于0，有问题请重新更改价格");
        }

        int[] orderStatus={Const.OrderStatusEnum.DISDEAL.getCode(),Const.OrderStatusEnum.UNSUBMIT.getCode(),Const.OrderStatusEnum.DISPAY.getCode(),Const.OrderStatusEnum.UNDELIVERY.getCode()};
        boolean judge=false;
        for (int i=0;i<orderStatus.length;i++){
            if (orderStatus[i]==order.getStatus()){
                judge=true;
            }
        }
        if (judge==false){
            return ServerResponse.createByErrorMessage("该订单处于无法改价状态，请检查该订单状态或者让客户重新下单！");
        }
        Order updateOrder=new Order();
        updateOrder.setId(order.getId());
        updateOrder.setPayment(payment);

        int rowCount=orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount>0){
            return ServerResponse.createBySuccess("改价成功");
        }
        return ServerResponse.createByErrorMessage("改价失败，请联系技术人员");
    }

    public ServerResponse changeOrderPrice(Long orderNo,Integer productId,Integer productModelId, BigDecimal payment){
        return null;
    }

    private DrawbackVo assembleDrawbackVo(Drawback drawback){
        DrawbackVo drawbackVo=new DrawbackVo();

        Order order=orderMapper.selectByOrderNo(drawback.getOrderNo());

        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(drawback.getOrderNo(),drawback.getUserId());
        OrderVo orderVo = assembleOrderVo(order,orderItemList);

        drawbackVo.setId(drawback.getId());
        drawbackVo.setOrderTime(DataTimeUtil.dateToStr(drawback.getOrderTime()));
        drawbackVo.setUserId(drawback.getUserId());
        drawbackVo.setOrderNo(drawback.getOrderNo());
        drawbackVo.setServiceType(drawback.getServiceType());
        drawbackVo.setServiceTypeDesc(Const.ServiceTypeEnum.codeOf(drawbackVo.getServiceType()).getValue());
        drawbackVo.setDrawbackMoney(drawback.getDrawbackMoney());
        drawbackVo.setReason(drawback.getReason());
        drawbackVo.setReasonDesc(Const.ReasonTypeEnum.codeOf(drawbackVo.getReason()).getValue());
        drawbackVo.setRefundWay(drawback.getRefundWay());
        drawbackVo.setRefundWayDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        drawbackVo.setDescription(drawback.getDescription());
        drawbackVo.setOrderVo(orderVo);

        return  drawbackVo;
    }

    public ServerResponse fpList(Integer userId,Integer pageNum,Integer pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Invoice> invoiceList=invoiceMapper.selectByUserId(userId);
        List<InvoiceVo> invoiceVoList=Lists.newArrayList();
        for(Invoice invoice:invoiceList){
            InvoiceVo invoiceVo=assembleinvoiceVo(invoice);
            invoiceVoList.add(invoiceVo);
        }
        PageInfo pageResult=new PageInfo(invoiceList);
        pageResult.setList(invoiceVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private InvoiceVo assembleinvoiceVo(Invoice invoice){
        InvoiceVo invoiceVo=new InvoiceVo();

        invoiceVo.setFpLsh(invoice.getFpLsh());
        invoiceVo.setKprq(DataTimeUtil.dateToStr(invoice.getKprq()));
        invoiceVo.setFpDm(invoice.getFpDm());
        invoiceVo.setFpHm(invoice.getFpHm());
        invoiceVo.setFpGf(invoice.getFpGf());
        invoiceVo.setFpGftax(invoice.getFpGftax());
        invoiceVo.setFpXf(invoice.getFpXf());
        invoiceVo.setFpTax(invoice.getFpTax());
        invoiceVo.setPdfBdurl(invoice.getPdfBdurl());
        invoiceVo.setHjbhsje(invoice.getHjbhsje());
        invoiceVo.setKphjse(invoice.getKphjse());

        return invoiceVo;
    }

    public ServerResponse sendMail(String mail,Long orderNo){
        Invoice invoice=invoiceMapper.selectByOrderNo(orderNo);
        if (invoice==null){
            return ServerResponse.createByErrorMessage("该订单未开具发票，请确认！");
        }

        MailBean mb = new MailBean();
        mb.setHost(PropertiesUtil.getProperty("mail.host")); // 设置SMTP主机(163)，若用126，则设为：smtp.126.com

        mb.setUsername(PropertiesUtil.getProperty("mail.user")); // 设置发件人邮箱的用户名
        mb.setPassword(PropertiesUtil.getProperty("mail.password")); // 设置发件人邮箱的密码，需将*号改成正确的密码
        mb.setFrom(PropertiesUtil.getProperty("mail.form")); // 设置发件人的邮箱
        mb.setTo(mail); // 设置收件人的邮箱
        String title="【南京思贝丽】您有一张电子发票 [发票号码："+String.valueOf(invoice.getFpHm())+"]";
        try {
            mb.setSubject(MimeUtility.encodeText(title,MimeUtility.mimeCharset("gb2312"), null));
        } catch (UnsupportedEncodingException e) {
            mb.setSubject("dzfp");
        }
        String content="尊敬的客户您好：\n" +
                "\n" +
                "您的订单号为："+String.valueOf(orderNo)+"选择开具电子发票，我们将电子发票发送给您，以便作为您的维权保修 凭证、报销凭证。\n"
                +"发票信息如下：\n" +
                "开票时间："+DataTimeUtil.dateToStr(invoice.getKprq())+"\n" +
                "发票代码："+invoice.getFpDm()+"\n" +
                "发票号码："+invoice.getFpHm()+"\n" +
                "销方名称："+invoice.getFpXf()+"\n" +
                "购方名称："+invoice.getFpGf()+"\n" +
                "价税合计：￥"+String.valueOf(invoice.getHjbhsje().add(invoice.getKphjse()))+"\n" +
                "附件是电子发票PDF文件，供下载使用。";
        mb.setContent(content); // 设置邮件的正文

        mb.attachFile("C:\\ftpfile\\img\\dzfp\\"+invoice.getFpLsh()+"1234567"+".pdf"); // 往邮件中添加附件


        SendMailUtil sm = new SendMailUtil();
        System.out.println("正在发送邮件...");
        // 发送邮件
        if (sm.sendMail(mb)){
            return ServerResponse.createBySuccess("发送邮件成功");
        }else{
            return ServerResponse.createByErrorMessage("发送邮件失败，请联系管理员");
        }
    }

    public ServerResponse kjfp(Long orderNo, String companyName,String gmtax){
        Invoice selectInvoice=invoiceMapper.selectByOrderNo(orderNo);
        if (selectInvoice!=null){
            return ServerResponse.createByErrorMessage("发票已开局，请勿重新申请！");
        }
        String xml = null;

        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order.getStatus()!=Const.OrderStatusEnum.DELIVERY.getCode()){
            return ServerResponse.createByErrorMessage("请您先确认收货，再尝试开具发票！");
        }
        List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(orderNo);

        try {
            xml = kjxml(String.valueOf(orderNo)+"1234567",order.getPayment(),orderItemList,companyName,gmtax);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("参数错误，请联系管理员！");
        }
        String xmlBack = HttpRequestUtils.httpPost(Const.url, xml);
        Invoice invoice=new Invoice();
        invoice.setUserId(order.getUserId());
        invoice.setFpLsh(String.valueOf(orderNo));
        invoice.setOrderNo(orderNo);
        invoice.setFpGf(companyName);
        invoice.setFpGftax(gmtax);
        invoiceMapper.insert(invoice);

        log.info("back::"+xmlBack);
        try {
            fpxz(String.valueOf(orderNo)+"1234567");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createBySuccess("开具发票成功");
    }

    private void fpxz(String lsh) throws Exception{
        log.info("发票下载中：");
        String xml = qzxml(lsh);
        String xmlBack = HttpRequestUtils.httpPost(Const.url, xml);
        String contentString = xmlBack.substring(xmlBack.indexOf("<content>")+9, xmlBack.indexOf("</content>"));
        String str = Gzip.gunzip(contentString);

        Map<String, String> map = new HashMap<String, String>();
        Document document = DocumentHelper.parseText(str);
        Element root = document.getRootElement();
        List<Element> elements = root.elements();
        for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
            Element element = it.next();
            map.put(element.getName(),element.getText());
        }

        Invoice invoice=invoiceMapper.selectByOrderNo(Long.valueOf(lsh.substring(0,13)));
        if (generateImage(map.get("EWM"),lsh+".jpg")){
            invoice.setEwmUrl("http://pic.99sbl.com/ewm/"+lsh+".jpg");
        }
        invoice.setKprq(DataTimeUtil.strToDate(map.get("KPRQ")));
        invoice.setFpDm(map.get("FP_DM"));
        invoice.setFpHm(map.get("FP_HM"));
        invoice.setFpXf(Const.XHFMC);
        invoice.setFpTax(Const.tax);
        invoice.setPdfUrl(map.get("PDF_URL"));
        invoice.setPdfBdurl("http://pic.99sbl.com/dzfp/"+lsh+".pdf");
        invoice.setHjbhsje(new BigDecimal(map.get("HJBHSJE")).setScale(2, BigDecimal.ROUND_HALF_UP));
        invoice.setKphjse(new BigDecimal(map.get("KPHJSE")).setScale(2, BigDecimal.ROUND_HALF_UP));
        invoiceMapper.updateByPrimaryKeySelective(invoice);

        //下载发票pdf
        String file = str.substring(str.indexOf("<PDF_FILE>")+10, str.indexOf("</PDF_FILE>")).replace("&#xd;","");
        System.out.println(" >>>>> "+file);
        FileUtil.SavePDF(lsh, file, Const.fpDownUrl);
    }

    private  String qzxml(String lsh) throws UnsupportedEncodingException{
        String dzfpkj = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<interface xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://www.chinatax.gov.cn/tirip/dataspec/interfaces.xsd\" "
                + "version=\"DZFP1.0\">"
                + "<globalInfo>"
                + "<terminalCode>0</terminalCode>"
                + "<appId>ZZS_PT_DZFP</appId>"
                + "<version>2.0</version>"
                + "<interfaceCode>ECXML.FPXZ.CX.E_INV</interfaceCode>"//接口编码
                + "<requestCode>" + Const.DSPTBM + "</requestCode>"
                + "<requestTime>2017-10-13 15:51:38 837</requestTime>"
                + "<responseCode>144</responseCode>"//数据交换请求接收方代码：固定144
                + "<dataExchangeId>"+lsh+"</dataExchangeId>"//流水号
                + "<userName>" + Const.DSPTBM + "</userName>"
                + "<passWord>"+Const.passWord+"</passWord>"//-------平台密码
                + "<taxpayerId>" + Const.tax + "</taxpayerId>"//--------------------------企业税号
                + "<authorizationCode>"+Const.authorizationCode+"</authorizationCode>"//-------------注册后的授权码
                + "<fjh></fjh>"
                + "</globalInfo>"
                + "<returnStateInfo>"
                + "<returnCode/>"
                + "<returnMessage/>"
                + "</returnStateInfo>"
                + "<Data>"
                + "<dataDescription>"
                + "<zipCode>0</zipCode>"//压缩标识
                + "<encryptCode>0</encryptCode>"//加密标识
                + "<codeType>0</codeType>"//加密方式
                + "</dataDescription>"
                + "<content>" + qzInner(lsh) + "</content>"//拼接内部报文
                + "</Data>"
                + "</interface>";
        System.out.println("报文："+dzfpkj);
        return dzfpkj;
    }

    private String qzInner(String lsh) throws UnsupportedEncodingException{
        String content_LS = "<REQUEST_FPXXXZ_NEW class=\"REQUEST_FPXXXZ_NEW\">"
                + "<FPQQLSH>"+lsh+"</FPQQLSH>"//发票请求唯一流水号
                + "<DSPTBM>" + Const.DSPTBM + "</DSPTBM>"//-------------平台编码
                + "<NSRSBH>" + Const.tax + "</NSRSBH>"//--------------开票方税号
                + "<DDH>27121550</DDH>"//订单号   非必填
                + "<PDF_XZFS>3</PDF_XZFS>"//PDF下载方式  详见接口文档
                + "</REQUEST_FPXXXZ_NEW>";
        System.out.println("明文："+content_LS);

        Base64 base64=new Base64();
        return new String(base64.encode(content_LS.getBytes("UTF-8")), "UTF-8");
    }

    private String kjxml(String lsh,BigDecimal payment,List<OrderItem> orderItemList,String companyName,String gmtax) throws UnsupportedEncodingException{
        String dzfpkj = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<interface xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://www.chinatax.gov.cn/tirip/dataspec/interfaces.xsd\" "
                + "version=\"DZFP1.0\">"
                + "<globalInfo>"
                + "<terminalCode>0</terminalCode>"//终端类型标识(0:B/S请求来源;1:C/S请求来源)
                + "<appId>ZZS_PT_DZFP</appId>"
                + "<version>2.0</version>"
                + "<interfaceCode>ECXML.FPKJ.BC.E_INV</interfaceCode>"//-----接口编码
                + "<requestCode>"+Const.DSPTBM+"</requestCode>"//-------------------平台编码
                + "<requestTime>2017-12-30 08:00:00 55</requestTime>"
                + "<responseCode>144</responseCode>" //数据交换请求接收方代码：固定144
                + "<dataExchangeId>"+lsh+"</dataExchangeId>"//流水号
                + "<userName>"+Const.DSPTBM+"</userName>"//-----------------平台编码
                + "<passWord>"+Const.passWord+"</passWord>"//---------------平台密码
                + "<taxpayerId>" + Const.tax + "</taxpayerId>"//-----------------企业税号
                + "<authorizationCode>"+Const.authorizationCode+"</authorizationCode>"//------------------注册后的授权码
                + "<fjh></fjh>"
                + "</globalInfo>"
                + "<returnStateInfo>"
                + "<returnCode/>"
                + "<returnMessage/>"
                + "</returnStateInfo>"
                + "<Data>"
                + "<dataDescription>"
                + "<zipCode>0</zipCode>"//压缩标识
                + "<encryptCode>0</encryptCode>"//加密标识
                + "<codeType>0</codeType>"//加密方式
                + "</dataDescription>"
                + "<content>" + kjInner(lsh,payment,orderItemList,companyName,gmtax) + "</content>"//拼接内部报文
                + "</Data>"
                + "</interface>";
        log.info("报文："+dzfpkj);
        return dzfpkj;
    }

    private String kjInner(String lsh,BigDecimal payment,List<OrderItem> orderItemList,String companyName,String gmtax) throws UnsupportedEncodingException{
        String content_LS = "<REQUEST_FPKJXX class=\"REQUEST_FPKJXX\">"
                + "<FPKJXX_FPTXX class=\"FPKJXX_FPTXX\">"
                + "<FPQQLSH>"+lsh+"</FPQQLSH>"//流水号、建议与外层保持一致
                + "<DSPTBM>"+Const.DSPTBM+"</DSPTBM>"//---------------------平台编码，与外层保持一致
                + "<NSRSBH>" + Const.tax + "</NSRSBH>"//------------------企业税号，与外层一致
                + "<NSRMC>"+Const.XHFMC+"</NSRMC>" //---------------------企业名称
                + "<NSRDZDAH></NSRDZDAH>"//开票方电子档案号(不是必填)
                + "<SWJG_DM></SWJG_DM>"
                + "<DKBZ>0</DKBZ>" //代开标志(0：自开   1：代开)
                + "<PYDM>000001</PYDM>"//固定值、勿修改！(票样代码)
                + "<KPXM>办公用品</KPXM>"//开票项目    主要开票商品，或者第一条商品，取项目信息中第一条数据的项目名称（或传递大类例如：办公用品）
                + "<XHF_NSRSBH>" + Const.tax + "</XHF_NSRSBH>"
                + "<XHFMC>"+Const.XHFMC+"</XHFMC>"
                + "<XHF_DZ>"+Const.XHF_DZ+"</XHF_DZ>"
                + "<XHF_DH>"+Const.XHF_DH+"</XHF_DH>"
                + "<XHF_YHZH>"+Const.XHF_YHZH+"</XHF_YHZH>"
                + "<GHFMC>"+companyName+"</GHFMC>" //购买方名称
                + "<GHF_NSRSBH>"+gmtax+"</GHF_NSRSBH>"//购买方税号
                + "<GHF_SF></GHF_SF>"
                + "<GHF_DZ></GHF_DZ>"
                + "<GHF_GDDH></GHF_GDDH>"
                + "<GHF_SJ>13616200405</GHF_SJ>"
                + "<GHF_EMAIL></GHF_EMAIL>"
                + "<GHFQYLX>01</GHFQYLX>"//购货方企业类型
                + "<GHF_YHZH></GHF_YHZH>"
                + "<HY_DM></HY_DM>"
                + "<HY_MC></HY_MC>"
                + "<KPY>"+Const.kpr+"</KPY>" //开票人
                + "<SKY></SKY>"
                + "<FHR></FHR>"
                + "<KPRQ></KPRQ>"
                + "<KPLX>1</KPLX>"//---------------------------开票类型：1正票、2红票
                + "<YFP_DM></YFP_DM>"
                + "<YFP_HM></YFP_HM>"
                + "<CZDM>10</CZDM>"
                + "<CHYY></CHYY>"
                + "<TSCHBZ>0</TSCHBZ>"
                + "<KPHJJE>"+String.valueOf(payment)+"</KPHJJE>"
                + "<HJBHSJE>"+String.valueOf(payment.subtract(getBigdecimalSE(payment)))+"</HJBHSJE>"
                + "<HJSE>"+getSE(payment)+"</HJSE>"
                + "<BZ></BZ>"
                + "<QDBZ>0</QDBZ>" //清单标志：默认为0
                + "<QD_BZ>0</QD_BZ>" //清单标志：默认为0
                + "<QDXMMC></QDXMMC>" //清单项目名称：清单标志为0时不做处理
                + "<FJH></FJH>"
                + "<BMB_BBH>18.0</BMB_BBH>" //编码表版本号：当前为18.0，这个不重要
                + "</FPKJXX_FPTXX>"
                +assembleXmxxs(orderItemList)
                //订单信息
                + "<FPKJXX_DDXX class=\"FPKJXX_DDXX\" size=\"1\">"
                + "<DDH></DDH>"
                + "<THDH></THDH>"
                + "<DDDATE></DDDATE>"
                + "<DDLX></DDLX>"
                + "</FPKJXX_DDXX>"
                //订单明细信息
                + "<FPKJXX_DDMXXXS class=\"FPKJXX_DDMXXX;\" size=\"0\"/>"
                //支付信息
                + "<FPKJXX_ZFXX class=\"FPKJXX_ZFXX\"/>"
                //物流信息
                + "<FPKJXX_WLXX class=\"FPKJXX_WLXX\"/>"
                + "</REQUEST_FPKJXX>";

        log.info("明文："+content_LS);

        Base64 base64=new Base64();
        return new String(base64.encode(content_LS.getBytes("UTF-8")), "UTF-8");
    }

    private String assembleXmxxs(List<OrderItem> orderItemList){
        String xmxxs="<FPKJXX_XMXXS class=\"FPKJXX_XMXX;\" size=\"1\">";
        for (OrderItem orderItem:orderItemList){
            String modelName="";
            String modelUnit="";
            if (orderItem.getModelUnit()!=null){
                modelUnit=orderItem.getModelUnit();
            }
            if (orderItem.getModelName()!=null){
                modelName=orderItem.getModelName();
            }
            String xmxx="<FPKJXX_XMXX>"
                    + "<XMMC><![CDATA["+"联想启天M410商用台式机"+"]]></XMMC>" //格式为   商品名称      如有多条明细，第一条的名称与上面的名称一致(发票的票面上货物或服务名称)
                    + "<XMDW><![CDATA["+modelUnit+"]]></XMDW>"
                    + "<GGXH><![CDATA["+modelName+"]]></GGXH>"
                    + "<XMSL>"+String.valueOf(orderItem.getQuantity())+"</XMSL>"
                    + "<HSBZ>1</HSBZ>"
                    + "<XMDJ>"+String .valueOf(orderItem.getCurrentUnitPrice())+"</XMDJ>"
                    + "<XMBM></XMBM>"
                    + "<XMJE>"+String.valueOf(orderItem.getTotalPrice())+"</XMJE>"
                    + "<SL>"+String.valueOf(Const.sl)+"</SL>"
                    + "<SE>"+getSE(orderItem.getTotalPrice())+"</SE>"
                    + "<SPHXZ>0</SPHXZ>"
                    + "<ZKHS></ZKHS>"
                    + "<FPHXZ>0</FPHXZ>"//发票行性质(0：正常化    1：折扣行      2：被折扣行)
                    + "<SPBM>1100301010000000000</SPBM>"
                    + "<ZXBM></ZXBM>"
                    + "<YHZCBS>0</YHZCBS>"
                    + "<LSLBS></LSLBS>"
                    + "<ZZSTSGL></ZZSTSGL>"
                    + "</FPKJXX_XMXX>";
            xmxxs=xmxxs+xmxx;
        }
        xmxxs=xmxxs+ "</FPKJXX_XMXXS>";
        return xmxxs;
    }

    private String getSE(BigDecimal payment){
        BigDecimal se= BigDecimalUtil.add(Const.sl,1);
        BigDecimal bhse=payment.subtract(payment.divide(se, Const.Decimal_digits,BigDecimal.ROUND_HALF_UP));
        String SE=String.valueOf(bhse);
        return SE;
    }

    private BigDecimal getBigdecimalSE(BigDecimal payment){
        BigDecimal se= BigDecimalUtil.add(Const.sl,1);
        BigDecimal bhse=payment.subtract(payment.divide(se, Const.Decimal_digits,BigDecimal.ROUND_HALF_UP));
        return bhse;
    }

    private boolean generateImage(String imgStr,String filename){
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for(int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream("C:\\ftpfile\\img\\ewm\\"+filename);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
