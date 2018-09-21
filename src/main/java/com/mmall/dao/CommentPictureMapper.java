package com.mmall.dao;

import com.mmall.pojo.CommentPicture;

public interface CommentPictureMapper {
    int deleteByPrimaryKey(Integer commentPictureId);

    int insert(CommentPicture record);

    int insertSelective(CommentPicture record);

    CommentPicture selectByPrimaryKey(Integer commentPictureId);

    int updateByPrimaryKeySelective(CommentPicture record);

    int updateByPrimaryKey(CommentPicture record);
}