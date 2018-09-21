package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;

public interface IMessageService {

    ServerResponse<PageInfo> list(Integer pageNum,Integer pageSize,Integer userId);

    ServerResponse add(String userName,String title,String content,String messageUrl,int goalUser);

    ServerResponse getNum(Integer userId);

    ServerResponse setRead(Integer userId,int id);

    ServerResponse deleteAll(Integer userId);

    ServerResponse deleteRead(Integer userId);

}
