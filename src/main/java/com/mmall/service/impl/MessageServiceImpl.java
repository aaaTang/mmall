package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.EnterUserMapper;
import com.mmall.dao.MessageMapper;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.EnterUser;
import com.mmall.pojo.Message;
import com.mmall.service.IMessageService;
import com.mmall.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("iMessageService")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EnterUserMapper enterUserMapper;

    public ServerResponse<PageInfo> list(Integer pageNum,Integer pageSize,Integer userId){
        List<Message> messageList=messageMapper.selectByUserId(userId);
        if (messageList==null){
            return ServerResponse.createByErrorMessage("用户消息列表为空");
        }
        List<MessageVo> messageVoList= Lists.newArrayList();
        for(Message message:messageList){
            MessageVo messageVo=assembleMessageVo(message);
            messageVoList.add(messageVo);
        }
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageResult=new PageInfo();
        pageResult.setList(messageVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse add(String userName,String title,String content,String messageUrl,int goalUser){
        if (goalUser!=0){
            Message message=new Message();
            message.setTitle(title);
            message.setContent(content);
            message.setMessageUrl(messageUrl);
            message.setOperator(userName);
            message.setRead(0);
            message.setGoalUser(goalUser);
            int rowCount=messageMapper.insert(message);
            if (rowCount>0){
                return ServerResponse.createBySuccess("插入消息成功");
            }
            return ServerResponse.createByErrorMessage("插入消息失败");
        }else {

            List<Integer> userIdList=userMapper.selectAllUserId();
            List<Integer> enterIdList=enterUserMapper.selectAllUserId();
            userIdList.addAll(enterIdList);

            List<Message> messageList=Lists.newArrayList();

            for (Integer userId:userIdList){
                Message message=new Message();
                message.setTitle(title);
                message.setContent(content);
                message.setMessageUrl(messageUrl);
                message.setOperator(userName);
                message.setRead(0);
                message.setGoalUser(userId);
                messageList.add(message);
            }

            int rowCount=messageMapper.batchInsert(messageList);
            if (rowCount>0){
                return ServerResponse.createBySuccess("插入消息成功");
            }
            return ServerResponse.createByErrorMessage("插入消息失败");

        }

    }

    public MessageVo assembleMessageVo(Message message){
        MessageVo messageVo=new MessageVo();
        messageVo.setId(message.getMessageInfoId());
        messageVo.setTitle(message.getTitle());
        messageVo.setContent(message.getContent());
        messageVo.setMessageUrl(message.getMessageUrl());
        messageVo.setOperator(message.getOperator());
        messageVo.setRead(message.getRead());
        messageVo.setCreateTime(message.getCreateTime());
        return messageVo;
    }

    public ServerResponse getNum(Integer userId){
        int count=messageMapper.selectNum(userId);
        return ServerResponse.createBySuccess(count);
    }

    public ServerResponse setRead(Integer userId,int id){

        if (id==0){
            messageMapper.setRead(userId);
            return ServerResponse.createBySuccess("设置成功");
        }
        else{
            Message message=new Message();
            message.setMessageInfoId(id);
            message.setRead(1);
            messageMapper.updateByPrimaryKeySelective(message);
            return ServerResponse.createBySuccess("设置成功");
        }
    }

    public ServerResponse deleteAll(Integer userId){
        messageMapper.deleteAll(userId);
        return ServerResponse.createBySuccess("删除成功");
    }

    public ServerResponse deleteRead(Integer userId){
        messageMapper.deleteRead(userId);
        return ServerResponse.createBySuccess("删除成功");
    }

}
