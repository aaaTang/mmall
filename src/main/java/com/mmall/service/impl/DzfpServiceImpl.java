package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.DzfpCartMapper;
import com.mmall.dao.DzfpItemMapper;
import com.mmall.dao.DzfpProductMapper;
import com.mmall.dao.InvoiceMapper;
import com.mmall.pojo.*;
import com.mmall.service.IDzfpService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.SendMailUtil;
import com.mmall.util.dzfp.FileUtil;
import com.mmall.util.dzfp.Gzip;
import com.mmall.util.dzfp.HttpRequestUtils;
import com.mmall.vo.DzfpCartProductVo;
import com.mmall.vo.DzfpProductVo;
import com.mmall.vo.InvoiceItemVo;
import com.mmall.vo.InvoiceVo;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("iDzfpService")

@Slf4j
public class DzfpServiceImpl implements IDzfpService {

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private DzfpCartMapper dzfpCartMapper;

    @Autowired
    private DzfpItemMapper dzfpItemMapper;

    @Autowired
    private DzfpProductMapper dzfpProductMapper;

    public ServerResponse searchProduct(String productName){
        productName="%"+productName+"%";
        List<DzfpProduct> dzfpProductList=dzfpProductMapper.selectByProductName(productName);
        List<DzfpProductVo> dzfpProductVoList=Lists.newArrayList();
        for (DzfpProduct dzfpProduct:dzfpProductList){
            DzfpProductVo dzfpProductVo=new DzfpProductVo();

            dzfpProductVo.setId(dzfpProduct.getId());
            dzfpProductVo.setProductName(dzfpProduct.getProductName());
            dzfpProductVo.setXinghao(dzfpProduct.getXinghao());

            dzfpProductVoList.add(dzfpProductVo);
        }
        return ServerResponse.createBySuccess(dzfpProductVoList);
    }

    public ServerResponse addProduct(Integer id,Integer productId,String productName,String unit,String model,Float count,BigDecimal price){
        if (id!=null){
            DzfpCart dzfpCart=dzfpCartMapper.selectByPrimaryKey(id);
            if (dzfpCart==null){
                return ServerResponse.createByErrorMessage("该id不存在！");
            }
            dzfpCart.setProductName(productName);
            dzfpCart.setUnit(unit);
            dzfpCart.setXinghao(model);
            dzfpCart.setCount(count);
            dzfpCart.setPrice(price);

            dzfpCartMapper.updateByPrimaryKeySelective(dzfpCart);
            return ServerResponse.createBySuccess("更新成功");
        }

        if (count==null){
            return ServerResponse.createByErrorMessage("请填写产品数量");
        }
        DzfpProduct dzfpProduct=dzfpProductMapper.selectByPrimaryKey(productId);
        if (dzfpProduct==null){
            return ServerResponse.createByErrorMessage("该产品不存在！");
        }
        if (productName!=null){
            dzfpProduct.setProductName(productName);
        }
        if (unit!=null){
            dzfpProduct.setUnit(unit);
        }
        if (model!=null){
            dzfpProduct.setXinghao(model);
        }
        if (price!=null){
            dzfpProduct.setPrice(price);
        }
        DzfpCart dzfpCart=assembleDzfpCart(dzfpProduct,count);
        int rowCount=dzfpCartMapper.insert(dzfpCart);
        if (rowCount>0){
            return ServerResponse.createBySuccess("商品插入成功！");
        }
        return ServerResponse.createByErrorMessage("商品插入失败！");

    }

    public ServerResponse deleteProduct(Integer productId){
        DzfpCart dzfpCart=dzfpCartMapper.selectByPrimaryKey(productId);
        if (dzfpCart==null){
            return ServerResponse.createByErrorMessage("该产品不存在！");
        }
        int rowCount=dzfpCartMapper.deleteByPrimaryKey(dzfpCart.getId());
        if (rowCount>0){
            return ServerResponse.createBySuccess("删除成功！");
        }
        return ServerResponse.createByErrorMessage("删除失败！");
    }

    private DzfpCart assembleDzfpCart(DzfpProduct dzfpProduct,Float count){
        DzfpCart dzfpCart=new DzfpCart();
        dzfpCart.setFpLsh(dzfpProduct.getSsflbm());
        dzfpCart.setProductName(dzfpProduct.getProductName());
        dzfpCart.setUnit(dzfpProduct.getUnit());
        dzfpCart.setXinghao(dzfpProduct.getXinghao());
        dzfpCart.setCount(count);
        dzfpCart.setPrice(dzfpProduct.getPrice());
        dzfpCart.setTotalPrice(BigDecimalUtil.mul(dzfpProduct.getPrice().doubleValue(),count));
        dzfpCart.setSl(new BigDecimal(dzfpProduct.getShuilv()));
        dzfpCart.setSe(BigDecimalUtil.mul(dzfpCart.getTotalPrice().doubleValue(),dzfpCart.getSl().doubleValue()));

        return dzfpCart;
    }

    public ServerResponse listProduct(){
        List<DzfpCart> dzfpCartList=dzfpCartMapper.selectAllDzfpCart();

        List<DzfpCartProductVo> dzfpCartProductVoList=assembleDzfpCartProductVo(dzfpCartList);
        return ServerResponse.createBySuccess(dzfpCartProductVoList);
    }

    private List<DzfpCartProductVo> assembleDzfpCartProductVo(List<DzfpCart> dzfpCartList){
        List<DzfpCartProductVo> dzfpCartProductVoList=Lists.newArrayList();
        for (DzfpCart dzfpCart:dzfpCartList){
            DzfpCartProductVo dzfpCartProductVo=new DzfpCartProductVo();

            dzfpCartProductVo.setId(dzfpCart.getId());
            dzfpCartProductVo.setUnit(dzfpCart.getUnit());
            dzfpCartProductVo.setXinghao(dzfpCart.getXinghao());
            dzfpCartProductVo.setCount(dzfpCart.getCount());
            dzfpCartProductVo.setPrice(dzfpCart.getPrice());
            dzfpCartProductVo.setProductName(dzfpCart.getProductName());
            dzfpCartProductVo.setTotalPrice(dzfpCart.getTotalPrice());
            dzfpCartProductVo.setSl(dzfpCart.getSl());

            dzfpCartProductVoList.add(dzfpCartProductVo);

        }
        return dzfpCartProductVoList;
    }

    public ServerResponse kjfp(String companyName, String gmtax,Integer kplx,String ylsh){

        Long orderNo=new OrderServiceImpl().generateOrderNo();
        String xml = null;

        if (kplx==1){
            List<DzfpCart> dzfpCartList=dzfpCartMapper.selectAllDzfpCart();
            if (CollectionUtils.isEmpty(dzfpCartList)){
                return ServerResponse.createByErrorMessage("开票产品为空！");
            }
            BigDecimal totalPrice=new BigDecimal("0");
            for (DzfpCart dzfpCart:dzfpCartList){
                totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),dzfpCart.getTotalPrice().doubleValue());
            }
            try{
                xml = kjxml(String.valueOf(orderNo)+"1234567",totalPrice,dzfpCartList,companyName,gmtax,kplx,ylsh);
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("参数错误，请联系管理员！");
            }
            String xmlBack = HttpRequestUtils.httpPost(Const.url, xml);
            String returnMessage=xmlBack.substring(xmlBack.indexOf("<returnMessage>")+15,xmlBack.indexOf("</returnMessage>"));
            if (returnMessage.equals("5Y+R56Wo5byA5YW35pWw5o2u5L+d5a2Y5oiQ5Yqf")){
                this.cleanDzfpCart(dzfpCartList);
                List<DzfpItem> dzfpItemList=assembleDzfpItemList(dzfpCartList,String.valueOf(orderNo));
                dzfpItemMapper.batchInsert(dzfpItemList);
                Invoice invoice=new Invoice();
                invoice.setFpLsh(String.valueOf(orderNo));
                invoice.setOrderNo(orderNo);
                invoice.setFpGf(companyName);
                invoice.setFpGftax(gmtax);
                invoice.setStatus(1);
                invoiceMapper.insert(invoice);
                return ServerResponse.createBySuccess("开具发票成功");
            }
            return ServerResponse.createByErrorMessage("开具发票失败");
        }

        if (kplx==2){

            try{
                xml = kjxml(String.valueOf(orderNo)+"1234567",null,null,companyName,gmtax,kplx,ylsh);
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("参数错误，请联系管理员！");
            }
            String xmlBack = HttpRequestUtils.httpPost(Const.url, xml);
            String returnMessage=xmlBack.substring(xmlBack.indexOf("<returnMessage>")+15,xmlBack.indexOf("</returnMessage>"));
            if (returnMessage.equals("5Y+R56Wo5byA5YW35pWw5o2u5L+d5a2Y5oiQ5Yqf")){
                Invoice invoice=invoiceMapper.selectByLsh(ylsh);
                invoice.setStatus(2);
                invoiceMapper.updateByPrimaryKeySelective(invoice);
                return ServerResponse.createBySuccess("红冲发票成功");
            }
            return ServerResponse.createByErrorMessage("红冲失败，请联系管理员，请勿重复点击红冲！");
        }
        return ServerResponse.createByErrorMessage("开票类型错误！");
    }

    private void cleanDzfpCart(List<DzfpCart> dzfpCartList){
        for (DzfpCart dzfpCart:dzfpCartList){
            dzfpCartMapper.deleteByPrimaryKey(dzfpCart.getId());
        }
    }

    private List<DzfpItem> assembleDzfpItemList(List<DzfpCart> dzfpCartList,String lsh){
        List<DzfpItem> dzfpItemList= Lists.newArrayList();
        for (DzfpCart dzfpCart:dzfpCartList){
            DzfpItem dzfpItem=new DzfpItem();

            dzfpItem.setFpLsh(lsh);
            dzfpItem.setFpFlh(dzfpCart.getFpLsh());
            dzfpItem.setProductName(dzfpCart.getProductName());
            dzfpItem.setUnit(dzfpCart.getUnit());
            dzfpItem.setXinghao(dzfpCart.getXinghao());
            dzfpItem.setCount(dzfpCart.getCount());
            dzfpItem.setPrice(dzfpCart.getPrice());
            dzfpItem.setTotalPrice(dzfpCart.getTotalPrice());
            dzfpItem.setSl(dzfpCart.getSl());
            dzfpItem.setSe(dzfpCart.getSe());

            dzfpItemList.add(dzfpItem);
        }
        return dzfpItemList;
    }

    public ServerResponse fpxz(String lsh) throws Exception{
        lsh=lsh+"1234567";
        log.info("发票下载中：");
        String xml = qzxml(lsh);
        String xmlBack = HttpRequestUtils.httpPost(Const.url, xml);
        String contentString = xmlBack.substring(xmlBack.indexOf("<content>")+9, xmlBack.indexOf("</content>"));

        String returnMessage=xmlBack.substring(xmlBack.indexOf("<returnMessage>")+15,xmlBack.indexOf("</returnMessage>"));
        if (returnMessage.equals("U1VDQ0VTUw==")){
            String str = Gzip.gunzip(contentString);

            Map<String, String> map = new HashMap<String, String>();
            Document document = DocumentHelper.parseText(str);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();
                map.put(element.getName(),element.getText());
            }
            Long tempOrderNo=Long.valueOf(lsh.substring(0,13));
            Invoice invoice=invoiceMapper.selectByOrderNo(tempOrderNo);
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
            FileUtil.SavePDF(lsh, file, Const.fpDownUrl);
            return ServerResponse.createBySuccess("下载成功");
        }
        return ServerResponse.createByErrorMessage("下载发票失败，请联系管理员，请勿重复点击下载！");

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

    private String kjxml(String lsh,BigDecimal payment,List<DzfpCart> dzfpCartList,String companyName,String gmtax,Integer kplx,String ylsh) throws UnsupportedEncodingException{
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
                + "<content>" + kjInner(lsh,payment,dzfpCartList,companyName,gmtax,kplx,ylsh) + "</content>"//拼接内部报文
                + "</Data>"
                + "</interface>";
        log.info("报文："+dzfpkj);
        return dzfpkj;
    }

    private String kjInner(String lsh,BigDecimal payment,List<DzfpCart> dzfpCartList,String companyName,String gmtax,Integer kplx,String ylsh) throws UnsupportedEncodingException{
        String KPLX="1";
        String YFP_DM="";
        String YFP_HM="";
        String CHYY="";
        String KPHJJE="";
        String HJBHSJE="";
        String HJSE="";
        String BZ="";

        if (kplx==1){
            KPHJJE=String.valueOf(payment);
            HJBHSJE=String.valueOf(payment.subtract(getBigdecimalSE(payment)));
            HJSE=getSE(payment);
        }

        if (kplx==2){
            Invoice invoice=invoiceMapper.selectByLsh(ylsh);
            KPLX="2";
            YFP_DM=invoice.getFpDm();
            YFP_HM=invoice.getFpHm();
            CHYY="错开发票";
            KPHJJE="-"+String.valueOf(BigDecimalUtil.add(invoice.getKphjse().doubleValue(),invoice.getHjbhsje().doubleValue()));
            HJBHSJE="-"+String.valueOf(invoice.getHjbhsje());
            HJSE="-"+String.valueOf(invoice.getKphjse());
            BZ="对应正数发票代码:"+YFP_DM+"号码:"+YFP_HM;
            companyName=invoice.getFpGf();
            gmtax=invoice.getFpGftax();
        }

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
                + "<KPLX>"+KPLX+"</KPLX>"//---------------------------开票类型：1正票、2红票
                + "<YFP_DM>"+YFP_DM+"</YFP_DM>"
                + "<YFP_HM>"+YFP_HM+"</YFP_HM>"
                + "<CZDM>10</CZDM>"
                + "<CHYY>"+CHYY+"</CHYY>"
                + "<TSCHBZ>0</TSCHBZ>"
                + "<KPHJJE>"+KPHJJE+"</KPHJJE>"
                + "<HJBHSJE>"+HJBHSJE+"</HJBHSJE>"
                + "<HJSE>"+HJSE+"</HJSE>"
                + "<BZ>"+BZ+"</BZ>"
                + "<QDBZ>0</QDBZ>" //清单标志：默认为0
                + "<QD_BZ>0</QD_BZ>" //清单标志：默认为0
                + "<QDXMMC></QDXMMC>" //清单项目名称：清单标志为0时不做处理
                + "<FJH></FJH>"
                + "<BMB_BBH>18.0</BMB_BBH>" //编码表版本号：当前为18.0，这个不重要
                + "</FPKJXX_FPTXX>"
                +assembleXmxxs(dzfpCartList,kplx,ylsh)
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

    private String assembleXmxxs(List<DzfpCart> dzfpCartList,Integer kplx,String ylsh){
        String xmxxs="<FPKJXX_XMXXS class=\"FPKJXX_XMXX;\" size=\"1\">";
        if (kplx==1){
            for (DzfpCart dzfpCart:dzfpCartList){
                String modelName="";
                String modelUnit="";
                String XMSL=String.valueOf(dzfpCart.getCount());
                String XMJE=String.valueOf(dzfpCart.getTotalPrice());
                String SE=String.valueOf(dzfpCart.getSe());
                if (dzfpCart.getUnit()!=null){
                    modelUnit=dzfpCart.getUnit();
                }
                if (dzfpCart.getXinghao()!=null){
                    modelName=dzfpCart.getXinghao();
                }
                String xmxx="<FPKJXX_XMXX>"
                        + "<XMMC><![CDATA["+dzfpCart.getProductName()+"]]></XMMC>" //格式为   商品名称      如有多条明细，第一条的名称与上面的名称一致(发票的票面上货物或服务名称)
                        + "<XMDW><![CDATA["+modelUnit+"]]></XMDW>"
                        + "<GGXH><![CDATA["+modelName+"]]></GGXH>"
                        + "<XMSL>"+XMSL+"</XMSL>"
                        + "<HSBZ>1</HSBZ>"
                        + "<XMDJ>"+String .valueOf(dzfpCart.getPrice())+"</XMDJ>"
                        + "<XMBM></XMBM>"
                        + "<XMJE>"+XMJE+"</XMJE>"
                        + "<SL>"+String.valueOf(dzfpCart.getSl())+"</SL>"
                        + "<SE>"+SE+"</SE>"
                        + "<SPHXZ>0</SPHXZ>"
                        + "<ZKHS></ZKHS>"
                        + "<FPHXZ>0</FPHXZ>"//发票行性质(0：正常化    1：折扣行      2：被折扣行)
                        + "<SPBM>"+generateSpbm(dzfpCart.getFpLsh())+"</SPBM>"
                        + "<ZXBM></ZXBM>"
                        + "<YHZCBS>0</YHZCBS>"
                        + "<LSLBS></LSLBS>"
                        + "<ZZSTSGL></ZZSTSGL>"
                        + "</FPKJXX_XMXX>";
                xmxxs=xmxxs+xmxx;
            }
        }
        if (kplx==2){
            List<DzfpItem> dzfpItemList=dzfpItemMapper.selectDzfpItemByLsh(ylsh);
            for(DzfpItem dzfpItem:dzfpItemList){
                String modelName="";
                String modelUnit="";
                String XMSL=String.valueOf(dzfpItem.getCount());
                String XMJE=String.valueOf(dzfpItem.getTotalPrice());
                String SE=String.valueOf(dzfpItem.getSe());
                if (dzfpItem.getUnit()!=null){
                    modelUnit=dzfpItem.getUnit();
                }
                if (dzfpItem.getXinghao()!=null){
                    modelName=dzfpItem.getXinghao();
                }
                XMSL="-"+XMSL;
                XMJE="-"+XMJE;
                SE="-"+SE;

                String xmxx="<FPKJXX_XMXX>"
                        + "<XMMC><![CDATA["+dzfpItem.getProductName()+"]]></XMMC>" //格式为   商品名称      如有多条明细，第一条的名称与上面的名称一致(发票的票面上货物或服务名称)
                        + "<XMDW><![CDATA["+modelUnit+"]]></XMDW>"
                        + "<GGXH><![CDATA["+modelName+"]]></GGXH>"
                        + "<XMSL>"+XMSL+"</XMSL>"
                        + "<HSBZ>1</HSBZ>"
                        + "<XMDJ>"+String .valueOf(dzfpItem.getPrice())+"</XMDJ>"
                        + "<XMBM></XMBM>"
                        + "<XMJE>"+XMJE+"</XMJE>"
                        + "<SL>"+String.valueOf(dzfpItem.getSl())+"</SL>"
                        + "<SE>"+SE+"</SE>"
                        + "<SPHXZ>0</SPHXZ>"
                        + "<ZKHS></ZKHS>"
                        + "<FPHXZ>0</FPHXZ>"//发票行性质(0：正常化    1：折扣行      2：被折扣行)
                        + "<SPBM>"+generateSpbm(dzfpItem.getFpFlh())+"</SPBM>"
                        + "<ZXBM></ZXBM>"
                        + "<YHZCBS>0</YHZCBS>"
                        + "<LSLBS></LSLBS>"
                        + "<ZZSTSGL></ZZSTSGL>"
                        + "</FPKJXX_XMXX>";
                xmxxs=xmxxs+xmxx;
            }
        }
        xmxxs=xmxxs+ "</FPKJXX_XMXXS>";
        return xmxxs;
    }

    private String generateSpbm(String spbm){
        int supple=19-spbm.length();
        String temStr="";
        for(int i=0;i<supple;i++){
            temStr+="0";
        }
        spbm+=temStr;
        return spbm;
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
            byte[] b = decoder.decodeBuffer(imgStr);
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

            e.printStackTrace();
        }
        return false;
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
                "您有一张电子发票，请您注意查收。\n"+
                "发票信息如下：\n" +
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

    public ServerResponse fpList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Invoice> invoiceList=invoiceMapper.selectByNoUserId();
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
        invoiceVo.setStatus(invoice.getStatus());
        invoiceVo.setStatusDesc(Const.InvoiceTypeEnum.codeOf(invoice.getStatus()).getValue());

        invoiceVo.setInvoiceItemVoList(assembleInvoiceItemVoList(invoice.getFpLsh()));

        return invoiceVo;
    }

    private List<InvoiceItemVo> assembleInvoiceItemVoList(String lsh){
        List<DzfpItem> dzfpItemList=dzfpItemMapper.selectDzfpItemByLsh(lsh);
        List<InvoiceItemVo> invoiceItemVoList=Lists.newArrayList();
        for (DzfpItem dzfpItem:dzfpItemList){

            InvoiceItemVo invoiceItemVo=new InvoiceItemVo();

            invoiceItemVo.setProductName(dzfpItem.getProductName());
            invoiceItemVo.setXinghao(dzfpItem.getXinghao());
            invoiceItemVo.setUnit(dzfpItem.getUnit());
            invoiceItemVo.setCount(dzfpItem.getCount());
            invoiceItemVo.setPrice(dzfpItem.getPrice());
            invoiceItemVo.setTotalPrice(dzfpItem.getTotalPrice());
            invoiceItemVo.setSl(dzfpItem.getSl());
            invoiceItemVo.setSe(dzfpItem.getSe());

            invoiceItemVoList.add(invoiceItemVo);
        }
        return invoiceItemVoList;
    }

}
