package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.EnterUser;

public interface IEnterUserService {

    ServerResponse<EnterUser> login(String username, String password);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,EnterUser enterUser);

    ServerResponse<EnterUser> updateInformation(EnterUser enterUser);
}
