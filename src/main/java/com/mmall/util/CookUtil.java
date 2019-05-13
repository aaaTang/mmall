package com.mmall.util;

import com.mmall.common.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookUtil {

    private static String COOKIE_DOMAIN=".99sbl.com";

    private static String COOKIE_NAME="mall_login_token";

    public static void writeLoginToken(HttpServletResponse httpServletResponse ,String token){

        Cookie cookie=new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");

        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存，只在当前页面有效；
        cookie.setMaxAge(Const.CookieCacheExtime.COOKIES_EXTIME);
        log.info("write cookie name:{},cookie value:{}",cookie.getName(),cookie.getValue());
        httpServletResponse.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest httpServletRequest){

        Cookie[] requestCookies=httpServletRequest.getCookies();
        if (requestCookies!=null){
            for (Cookie cookie:requestCookies){
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("read cookie name:{},cookie value:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Cookie[] requestCookies=httpServletRequest.getCookies();
        if (requestCookies!=null){
            for (Cookie cookie:requestCookies){
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    httpServletResponse.addCookie(cookie);
                    log.info("del cookie name:{},cookie value:{}",cookie.getName(),cookie.getValue());
                }
            }
        }
    }

}
