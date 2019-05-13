package com.mmall.controller.common.shiro;

import com.mmall.pojo.User;
import com.mmall.service.IShiroService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class MyShiroReaml extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user=(User)principalCollection.getPrimaryPrincipal();
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }

    private IShiroService shiroService;

    public IShiroService getShiroService() {
        return shiroService;
    }

    public void setShiroService(IShiroService shiroService) {
        this.shiroService = shiroService;
    }

}
