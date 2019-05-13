package com.mmall.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mmall.dao.TokenUserMapper;
import com.mmall.pojo.TokenUser;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/auth2/")
@Slf4j
public class GetTokenController {

    @Autowired
    TokenUserMapper tokenUserMapper;

    @RequestMapping(value="access_token",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getToken(String username,String password,String timestamp,String sign, HttpServletResponse httpServletResponse){

        String md5= DigestUtils.md5Hex(username+password+timestamp+password);
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.equals(sign,md5)){
            TokenUser tokenUser=tokenUserMapper.selectByUserNameAndPassword(username,password);
            try {
                int redisTime=DataTimeUtil.getExpireTime(timestamp,tokenUser.getExpires().getTime());
                String token= MD5Util.MD5EncodeUtf8(sign);
                RedisPoolUtil.setEx(token, JsonUtil.obj2String(tokenUser),redisTime);
                String expiresAt= DataTimeUtil.getExpireTimeAt(timestamp,12);

                jsonObject.put("success", true);
                jsonObject.put("access_token",token);
                jsonObject.put("expires_at",expiresAt);

                return jsonObject;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jsonObject.put("success",false);
        jsonObject.put("desc","参数错误！");
        return jsonObject;

    }

}
