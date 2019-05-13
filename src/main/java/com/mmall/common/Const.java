package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String skuSplit=",";

    public static final int average=5;
    public static final double medium=0.0;
    public static final double good=1.0;
    public static final double bad=0.0;


    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL="email";
    public static final String USERNAME="username";

    public static final int RANDOMPRODUCTNUM=15;

    public static final int LOVEPRODUCTNUM=10;

    public static final int Decimal_digits=2;

    //电子发票开具与下载相关参数；

    //开票税率
    public static final double sl=0.10;
    //开票地址
    public static final String url="http://jsfapiao.cn:9000/dianzifapiaoService/slqzconsole.do";
    //开票税号
    public static final String tax = "91320102771268806K";
    //开票人
    public static final String kpr="岳一军";
    //销货方名称
    public static final String XHFMC="南京思贝丽商贸有限公司";
    //销货方地址
    public static final String XHF_DZ="南京市玄武区汇文里12号";
    //销货方电话
    public static final String XHF_DH="84718591";
    //销货方银行账号
    public static final String XHF_YHZH="中国银行南京珠江路支行522267381131";
    //平台编码
    public static final String DSPTBM="v0bbdjYO";
    //平台密码
    public static final String passWord="3201000132MDgxMDMwYTA0YWQyOGMyMw==";
    //授权码
    public static final String authorizationCode="WQM8N5UPL1";
    //电子发票下载路径
    public static final String fpDownUrl="C:\\ftpfile\\img\\dzfp";

    public interface RedisCacheExtime{
        int REDIS_SEESION_EXTIME=60*30;//30分钟
    }

    public interface CookieCacheExtime{
        int COOKIES_EXTIME=60*60*12;//12小时，单位是秒
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

    public enum UserRoleEnum{

        PUTONG(-1,"普通用户"),
        PUTONG1(0,"普通用户"),
        YIJI(1,"一级审核员"),
        ERJI(2,"二级审核员"),
        SANJI(3,"三级审核员"),
        SIJI(4,"四级审核员"),
        WUJI(5,"管理员");

        UserRoleEnum(int code,String value){
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

        public static UserRoleEnum codeOf(int code){
            for (UserRoleEnum userRoleEnum : values()){
                if (userRoleEnum.getCode() == code){
                    return userRoleEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
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

    public enum ServiceTypeEnum{
        SALERETURN(1,"退货"),
        MONEYRETURN(2,"退款");

        ServiceTypeEnum(int code,String value){
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

        public static ServiceTypeEnum codeOf(int code){
            for (ServiceTypeEnum serviceTypeEnum : values()){
                if (serviceTypeEnum.getCode() == code){
                    return serviceTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }

    public enum ReasonTypeEnum{

        POSUN(1,"收到商品破损"),
        CUOFA(2,"商品错发/漏发"),
        WEIXIU(3,"商品需要维修"),
        FAPIAO(4,"发票问题"),
        MIAOSHU(5,"收到商品与描述不符"),
        ZHILIANG(6,"商品质量问题"),
        SHIJIAN(7,"未按约定时间发货"),
        JIAHUO(8,"收到假货"),
        WULIYOU(9,"无理由退货");

        ReasonTypeEnum(int code,String value){
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

        public static ReasonTypeEnum codeOf(int code){
            for (ReasonTypeEnum reasonTypeEnum : values()){
                if (reasonTypeEnum.getCode() == code){
                    return reasonTypeEnum;
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

    public enum InvoiceTypeEnum{
        KAIJU(1,"已开具"),
        HONGCHONG(2,"已红冲");

        InvoiceTypeEnum(int code,String value){
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

        public static InvoiceTypeEnum codeOf(int code){
            for (InvoiceTypeEnum invoiceTypeEnum : values()){
                if (invoiceTypeEnum.getCode() == code){
                    return invoiceTypeEnum;
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
