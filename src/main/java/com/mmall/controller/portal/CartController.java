package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart/")
@Slf4j
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString= RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user= JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                RedisPoolUtil.expire(token,Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
                return iCartService.list(user.getId());
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.list(enterUser.getEnterUserId());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest, Integer count, Integer productId,Integer modelId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.add(user.getId(),count,productId,modelId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.add(enterUser.getEnterUserId(),count,productId,modelId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest, Integer productId ,Integer count,Integer modelId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.update(user.getId(),productId,count,modelId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.update(enterUser.getEnterUserId(),productId,count,modelId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest, String productIds,@RequestParam(value ="modelId",defaultValue = "0") Integer modelId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.deleteProduct(user.getId(),productIds,modelId);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.deleteProduct(enterUser.getEnterUserId(),productIds,modelId);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),null,null,Const.Cart.CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),null,null,Const.Cart.CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> UnSelectAll(HttpServletRequest httpServletRequest){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),null,null,Const.Cart.UN_CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),null,null,Const.Cart.UN_CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),productId,null,Const.Cart.CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),productId,null,Const.Cart.CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> UnSelect(HttpServletRequest httpServletRequest,Integer productId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),productId,null,Const.Cart.UN_CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),productId,null,Const.Cart.UN_CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("select_model.do")
    @ResponseBody
    public ServerResponse<CartVo> selectModel(HttpServletRequest httpServletRequest,Integer productId,Integer modelId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),productId,modelId,Const.Cart.CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),productId,modelId,Const.Cart.CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("un_select_model.do")
    @ResponseBody
    public ServerResponse<CartVo> UnSelectModel(HttpServletRequest httpServletRequest,Integer productId,Integer modelId){
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.selectOrUnSelect(user.getId(),productId,modelId,Const.Cart.UN_CHECKED);
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.selectOrUnSelect(enterUser.getEnterUserId(),productId,modelId,Const.Cart.UN_CHECKED);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest) {
        String token= CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            EnterUser enterUser=JsonUtil.string2Obj(userString,EnterUser.class);
            if (user.getId()!=null){
                return iCartService.getCartProductCount(user.getId());
            }
            if (enterUser.getEnterUserId()!=null){
                return iCartService.getCartProductCount(enterUser.getEnterUserId());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
}
