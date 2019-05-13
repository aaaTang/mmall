package com.mmall.util.dzfp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * 
 * @author zy
 * 功能   模拟post请求
 *
 */
public class HttpRequestUtils {
	
	private final static Log log=LogFactory.getLog(HttpRequestUtils.class);    //日志记录
 
    /**
     * httpPost
     * @param url  路径
     * @param jsonParam 参数
     * @return
     */
    public static String httpPost(String url,String jsonParam){
        return httpPost(url, jsonParam, false);
    }
 
    /**
     * post请求
     * @param url         url地址
     * @param jsonParam     参数
     * @param noNeedResponse    不需要返回结果
     * @return
     */
    public static String httpPost(String url,String jsonParam, boolean noNeedResponse){
    	//System.out.println("发送报文:"+jsonParam);
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String strResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    //System.out.println("back:"+str);
                    strResult=str;
                    if (noNeedResponse) {
                        return null;
                    }
                } catch (Exception e) {
                	log.error("post请求提交失败:" + url+ e.getMessage());
                }
            }
        } catch (IOException e) {
        	log.error("post请求提交失败:" + url+ e.getMessage());
        }
        return strResult;
    }
 
}
