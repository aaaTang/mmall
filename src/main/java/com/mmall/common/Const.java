package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String TOKEN_PREFIX="token_";

    public static final String EMAIL="email";
    public static final String USERNAME="username";

    public static final int RANDOMPRODUCTNUM=15;

    public static final int LOVEPRODUCTNUM=10;

    public static final int Decimal_digits=2;

    public interface tokenCacheExtime{
        int TOKEN_CACHE_EXTIME=60*60*12; //一分钟
    }

    public interface RedisCacheExtime{
        int REDIS_SEESION_EXTIME=60*30;//30分钟
    }

    public interface ProductListOrderBy{

        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED=1; //即购物车选中状态；
        int UN_CHECKED=0; //即购物车未选中状态；

        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }



    public interface Role{
        int ROLE_CUSTOMER=0; //普通用户
        int ROLE_ADMIN=5; //管理员
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum{

        DISDEAL(-4,"订单未处理"),
        DELETE(-3,"订单删除"),
        CANCELED(-2,"订单取消"),
        UNSUBMIT(-1,"未提交审核"),
        ONCHECK(0,"正在审核"),
        FAILCHECK(1,"审核不通过"),
        SUCCESSCHECK(2,"审核通过"),
        EXPRESSON(3,"待收货"),
        DELIVERY(4,"已完成"),
        UNDELIVERY(5,"未发货"),
        DISPAY(6,"未付款"),
        YESPAY(7,"已付款"),
        DRAWBACK(8,"售后中"),
        EVALUATE(9,"已评价"),
        HAVAEVALUATE(10,"已追评");

        OrderStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";

        String RESPONSE_SUCCESS="success";
        String RESPONSE_FAILED="failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code=code;
            this.value=value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        UN_DEAL(0,"未处理"),
        ONLINE_PAY(1,"在线支付"),
        DELIVERY_PAY(2,"货到付款"),
        OFFLINE_PAY(3,"提交审核");
        PaymentTypeEnum(int code,String value){
            this.code=code;
            this.value=value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public enum ExpressTypeEnum{
        OWN_EXPRESS(1,"自家配送"),
        BEST_EXPRESS(2,"百世快递"),
        YIMI_EXPRESS(3,"壹米滴答");
        ExpressTypeEnum(int code,String value){
            this.code=code;
            this.value=value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static ExpressTypeEnum codeOf(int code){
            for (ExpressTypeEnum expressTypeEnum : values()){
                if (expressTypeEnum.getCode() == code){
                    return expressTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public enum CheckStatusEnum{
        ON_CHECK(0,"审核中"),
        FAIL_CHECK(1,"审核不通过"),
        SUCCESS_CHECK(2,"审核通过");
        CheckStatusEnum(int code,String value){
            this.code=code;
            this.value=value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static CheckStatusEnum codeOf(int code){
            for (CheckStatusEnum checkStatusEnum : values()){
                if (checkStatusEnum.getCode() == code){
                    return checkStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

}
