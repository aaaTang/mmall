package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IUserService;
import com.mmall.util.CookUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     *用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody

    public ServerResponse<User> login(HttpServletResponse httpServletResponse, String username, String password, HttpSession session){

        ServerResponse<User> response=iUserService.login(username,password);
        if (response.issuccess()){
            CookUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String token=CookUtil.readLoginToken(httpServletRequest);
        CookUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(token);
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
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (token!=null){
            String userString=RedisPoolUtil.get(token);
            if (StringUtils.isNotBlank(userString)){
                User user=JsonUtil.string2Obj(userString,User.class);
                user.setPassword(null);

                RedisPoolUtil.setEx(token, JsonUtil.obj2String(user),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
                return ServerResponse.createBySuccess(user);
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
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

    @RequestMapping(value="reset_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest,String userName,String passwordOld,String passwordNew){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (userString==null){
                return iUserService.resetPasswordById(userName,passwordOld,passwordNew);
            }
            User user=JsonUtil.string2Obj(userString,User.class);
            if (user.getId()!=null){
                return iUserService.resetPassword(passwordOld,passwordNew,user);
            }
            return iUserService.resetPasswordById(userName,passwordOld,passwordNew);
        }
        return iUserService.resetPasswordById(userName,passwordOld,passwordNew);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest,User user){
        String token=CookUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)){
            String userString=RedisPoolUtil.get(token);
            if (StringUtils.isNotBlank(userString)){
                User currentUser=JsonUtil.string2Obj(userString,User.class);
                if (currentUser!=null){
                    user.setId(currentUser.getId());
                    user.setUsername(currentUser.getUsername());
                    ServerResponse<User> response=iUserService.updateInformation(user);
                    if (response.issuccess()){
                        User updateUser=response.getData();
                        updateUser.setPassword("");
                        RedisPoolUtil.setEx(token,JsonUtil.obj2String(updateUser),Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
                        return response;
                    }
                    return ServerResponse.createByErrorMessage("更新失败，请联系管理员！");
                }
                return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

//    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<User> get_information(HttpSession session){
//        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
//        if (currentUser==null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
//        }
//        return iUserService.getInformation(currentUser.getId());
//    }

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

//    @RequestMapping("upload.do")
//    @ResponseBody
//    public ServerResponse upload(HttpSession session,@RequestParam(value="upload_file",required=false) MultipartFile file, HttpServletRequest request){
//
//        User user=(User)session.getAttribute(Const.CURRENT_USER);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
//        }
//        if (iUserService.checkAdminRole(user).issuccess()){
//            String path=request.getSession().getServletContext().getRealPath("upload");
//
//            String targetFileName=iFileService.upload(file,path);
//            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
//
//            Map fileMap= Maps.newHashMap();
//
//            fileMap.put("uri",targetFileName);
//            fileMap.put("url",url);
//
//            return ServerResponse.createBySuccess(fileMap);
//
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
//
//    }


}
