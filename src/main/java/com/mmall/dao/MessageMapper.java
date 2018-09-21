package com.mmall.dao;

import com.mmall.pojo.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    int deleteByPrimaryKey(Integer messageInfoId);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer messageInfoId);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    List<Message> selectByUserId(Integer userId);

    int selectNum(Integer userId);

    int setRead(Integer userId);

    int batchInsert(@Param("messageList") List<Message> messageList);

    int deleteAll(Integer userId);

    int deleteRead(Integer userId);
}
