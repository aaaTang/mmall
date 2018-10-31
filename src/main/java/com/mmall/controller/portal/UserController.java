package com.mmall.controller.portal;


import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileService;

    /**
     *用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody

    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){

        ServerResponse<User> response=iUserService.login(username,password);
        if (response.issuccess()){
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }


    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){

        return iUserService.checkValid(str,type);

    }

    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
        //User user=(User) session.getAttribute(Const.CURRENT_USER);

        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userStr,User.class);

        if (user!=null){
            user.setPassword(null);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }


    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session,User user){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser==null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response=iUserService.updateInformation(user);
        if (response.issuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){

        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userStr,User.class);

        if (user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(user.getId());
    }

//    @RequestMapping(value="upload.do")
//    @ResponseBody
//    public ServerResponse upload(HttpSession session, @RequestParam(value="upload_file",required=false) MultipartFile file, HttpServletRequest request){
//        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
//        if (currentUser==null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
//        }
//        String path=request.getSession().getServletContext().getRealPath("upload");
//        String targetFileName=iFileService.uploadImg(file,path);
//        String url= PropertiesUtil.getProperty("ftp.server.headimg.prefix")+targetFileName;
//        Map fileMap= Maps.newHashMap();
//
//        fileMap.put("uri",targetFileName);
//        fileMap.put("url",url);
//
//        currentUser.setHeadImg(url);
//        ServerResponse<User> response=iUserService.updateInformation(currentUser);
//        if (response.issuccess()){
//            session.setAttribute(Const.CURRENT_USER,response.getData());
//        }
//        return ServerResponse.createBySuccess(url);
//    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest httpServletRequest,@RequestParam(value="upload_file",required=false) MultipartFile file, HttpServletRequest request){

        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userStr=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userStr,User.class);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if (iUserService.checkAdminRole(user).issuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");

            String targetFileName=iFileService.upload(file,path);
            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap= Maps.newHashMap();

            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);

        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }


}
