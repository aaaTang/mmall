package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.EnterUserMapper;
import com.mmall.dao.LoginRecordMapper;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.LoginRecord;
import com.mmall.service.IUserService;
import com.mmall.pojo.User;
import com.mmall.util.MD5Util;
import com.mmall.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginRecordMapper loginRecordMapper;

    @Autowired
    private EnterUserMapper enterUserMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        LoginRecord loginRecord=new LoginRecord();

        loginRecord.setUsername(username);
        loginRecord.setPassword(password);
        loginRecord.setType(1);
        loginRecord.setIssuccess(1);

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            loginRecord.setIssuccess(2);
            loginRecordMapper.insert(loginRecord);
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if (user==null){
            loginRecord.setIssuccess(2);
            loginRecordMapper.insert(loginRecord);
            return ServerResponse.createByErrorMessage("密码错误");
        }
        loginRecordMapper.insert(loginRecord);
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.issuccess()){
            return validResponse;
        }

        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if (!validResponse.issuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setDiscount(100);
        user.setRole(0);
        int resultCount=userMapper.insert(user);

        if (resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if (resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");

        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.issuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question=userMapper.selectQuestionByUsername(username);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            //说明问题及答案是这个用户的，并且是正确的
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);

        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }


    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.issuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if (org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);

            if (rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");

    }

    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count（1），如果不指定id，那么结果就是true了。
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if (resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<String> resetPasswordById(String userName,String passwordOld,String passwordNew){
        int userNameCount=userMapper.checkUsername(userName);
        if (userNameCount>0){
            User user=userMapper.selectByUserName(userName);
            int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
            if (resultCount==0){
                return ServerResponse.createByErrorMessage("用户名或密码错误");
            }
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int updateCount=userMapper.updateByPrimaryKeySelective(user);
            if (updateCount>0){
                return ServerResponse.createBySuccessMessage("密码更新成功");
            }
            return ServerResponse.createByErrorMessage("密码更新失败");
        }
        EnterUser enterUser=enterUserMapper.selectByenterCoding(userName);
        if (enterUser==null){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        int enterResultCount=enterUserMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),enterUser.getEnterUserId());
        if (enterResultCount==0){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        enterUser.setEnterUserPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=enterUserMapper.updateByPrimaryKeySelective(enterUser);
        if (updateCount>0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");

    }

    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的；
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话不能是当前用户的。
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email再进行尝试");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setHeadImg(user.getHeadImg());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);

        if (updateCount>0){
            updateUser=userMapper.selectByPrimaryKey(updateUser.getId());
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){

        User user =userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //backend

    /**
     * 校验是否为管理员
     * @param user
     * @return
     */

    public ServerResponse checkAdminRole(User user){
        if (user!=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse getUserList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList=userMapper.selectAllUser();
        List<UserVo> userVoList= Lists.newArrayList();
        for (User user:userList){
            UserVo userVo=assembleUserVo(user);
            userVoList.add(userVo);
        }
        PageInfo pageResult=new PageInfo(userList);
        pageResult.setList(userVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private UserVo assembleUserVo(User user){
        UserVo userVo=new UserVo();

        userVo.setId(user.getId());
        userVo.setUsername(user.getUsername());
        userVo.setEmail(user.getEmail());
        userVo.setPhone(user.getPhone());
        userVo.setQuestion(user.getQuestion());
        userVo.setAnswer(user.getAnswer());
        userVo.setRole(user.getRole());
        userVo.setRoleDesc(Const.UserRoleEnum.codeOf(user.getRole()).getValue());
        userVo.setDiscount(user.getDiscount());
        userVo.setHeadImg(user.getHeadImg());

        return userVo;
    }


}
