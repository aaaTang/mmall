package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CommentVo;

import java.util.List;

public interface ICommentService {

    ServerResponse add(int userId, int productId, int typeId, Long orderNo, int orderItemId, int starLevel, String content);

    ServerResponse<List<CommentVo>> list(int productId);

    ServerResponse addNewContent(int userId, int commentId, String newContent);
}
