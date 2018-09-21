package com.mmall.filter;

import com.mmall.common.ServerResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebContextFileter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
        //TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String []  allowDomain= {"http://localhost:8080","http://mall.99sbl.com","http://b2b.99sbl.com"};
        Set allowedOrigins= new HashSet(Arrays.asList(allowDomain));
        String originHeader=((HttpServletRequest) request).getHeader("Origin");

        if (allowedOrigins.contains(originHeader)){
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", originHeader);
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");//表明服务器支持的所有头信息字段
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true"); //如果要把Cookie发到服务器，需要指定Access-Control-Allow-Credentials字段为true;
            ((HttpServletResponse) response).setHeader("XDomainRequestAllowed","1");
        }
        httpServletResponse.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
        chain.doFilter(request,httpServletResponse);
        return;
    }

    @Override
    public void destroy() {

    }


}
