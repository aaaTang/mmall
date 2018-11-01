package com.mmall.controller.common.interceptor;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthrityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        //HandlerMethod handlerMethod=(HandlerMethod) o;

        //开始解析handerMethod
        //String methodName=handlerMethod.getMethod().getName();
        //String className=handlerMethod.getBean().getClass().getSimpleName();

        //解析参数，具体的参数key和value，并打印日志；
        StringBuffer requestParamBuffer=new StringBuffer();
        Map paramMap=httpServletRequest.getParameterMap();

        Iterator iterator=paramMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry=(Map.Entry)iterator.next();
            String mapkey=(String)entry.getKey();

            String mapValue=StringUtils.EMPTY;

            //request这个参数的map，里面的value返回的是一个String[]
            Object obj=entry.getValue();
            if (obj instanceof String[]){
                String [] strs=(String[])obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapkey).append("=").append(mapValue);

        }

        User user=null;
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
            String userStr=RedisPoolUtil.get(loginToken);
            user=JsonUtil.string2Obj(userStr,User.class);
        }
        if (user==null || user.getRole().intValue()!= Const.Role.ROLE_ADMIN){
            httpServletResponse.reset(); //这里要添加reset，否则报异常，getWriter()
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            PrintWriter out=httpServletResponse.getWriter();
            if (user==null){
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"拦截器拦截,该用户未登录")));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，该用户非管理员")));
            }
            out.flush();
            out.close();

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");  //
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");  //所有处理完成之后
    }
}
