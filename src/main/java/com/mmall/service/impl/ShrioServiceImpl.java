package com.mmall.service.impl;

import com.mmall.dao.ShiroMapper;
import com.mmall.pojo.Permission;
import com.mmall.pojo.User;
import com.mmall.service.IShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iShiroService")
public class ShrioServiceImpl implements IShiroService{

    @Autowired
    private ShiroMapper shiroDao;

    public User getUserByUserName(String username) {
        //根据账号获取账号密码
        User userByUserName = shiroDao.getUserByUserName(username);
        return userByUserName;
    }

    public List<Permission> getPermissionsByUser(User user) {
        //获取到用户角色userRole
        List<Integer> roleId = shiroDao.getUserRoleByUserId(user.getId());
        List<Permission> perArrary = new ArrayList<>();

        if (roleId!=null&&roleId.size()!=0) {
            //根据roleid获取peimission
            for (Integer i : roleId) {
                perArrary.addAll(shiroDao.getPermissionsByRoleId(i));
            }
        }

        System.out.println(perArrary);
        return perArrary;
    }

}
