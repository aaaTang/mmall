package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.EnterUserMapper;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.User;
import com.mmall.service.IEnterUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iEnterUserService")
public class EnterUserServiceImpl implements IEnterUserService {

    @Autowired
    private EnterUserMapper enterUserMapper;

    public ServerResponse<EnterUser> login(String username, String password) {
        int resultCount = enterUserMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        EnterUser enterUser = enterUserMapper.selectLogin(username,md5Password);
        if (enterUser==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        enterUser.setEnterUserPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",enterUser);
    }

    public ServerResponse<String> checkValid(String str,String type){
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount=enterUserMapper.checkUsername(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount=enterUserMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");

        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,EnterUser enterUser){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count（1），如果不指定id，那么结果就是true了。
        int resultCount=enterUserMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),enterUser.getEnterUserId());
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        enterUser.setEnterUserPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=enterUserMapper.updateByPrimaryKeySelective(enterUser);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<EnterUser> updateInformation(EnterUser enterUser){
        //username是不能被更新的；
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话不能是当前用户的。
        int resultCount=enterUserMapper.checkEmailByUserId(enterUser.getFax(),enterUser.getEnterUserId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email再进行尝试");
        }
        EnterUser updateEnterUser=new EnterUser();
        updateEnterUser.setEnterUserId(enterUser.getEnterUserId());
        updateEnterUser.setEmerTelephone(enterUser.getEmerTelephone());
        updateEnterUser.setHeadImg(enterUser.getHeadImg());
        updateEnterUser.setPhone(enterUser.getPhone());
        updateEnterUser.setFax(enterUser.getFax());
        updateEnterUser.setQq(enterUser.getQq());
        updateEnterUser.setInvoiceAddress(enterUser.getInvoiceAddress());
        updateEnterUser.setInvoiceBank(enterUser.getInvoiceBank());
        updateEnterUser.setInvoiceCount(enterUser.getInvoiceCount());
        updateEnterUser.setInvoiceNumber(enterUser.getInvoiceNumber());
        updateEnterUser.setInvoicePhone(enterUser.getInvoicePhone());

        int updateCount=enterUserMapper.updateByPrimaryKeySelective(updateEnterUser);

        if (updateCount>0){
            updateEnterUser=enterUserMapper.selectByPrimaryKey(updateEnterUser.getEnterUserId());
            updateEnterUser.setEnterUserPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess("更新个人信息成功",updateEnterUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

}
