package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.ICommentService;
import com.mmall.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iCommentService")
@Slf4j
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EnterUserMapper enterUserMapper;

    @Autowired
    private ProductModelMapper productModelMapper;

    public ServerResponse add(int userId, int productId, int typeId,Long orderNo,int orderItemId, int starLevel, String content){

        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order==null||order.getStatus()!=4){
            return ServerResponse.createByErrorMessage("该订单不存在或处于未完成状态");
        }

        OrderItem orderItem=orderItemMapper.selectByIdAndOrderNo(orderItemId,orderNo);
        if (orderItem==null){
            return ServerResponse.createByErrorMessage("该订单产品id不存在");
        }

        Comment comment;
        comment=commentMapper.selectByOrderNoAndOrderItemId(orderNo,orderItemId);
        if (comment!=null){
            return ServerResponse.createByErrorMessage("该产品已经添加评论，请勿重复评论！");
        }

        if (typeId!=-1){
            ProductModel productModel=productModelMapper.selectByPrimaryKey(typeId);
            if (productModel==null){
                return ServerResponse.createByErrorMessage("该商品规格型号不存在");
            }
        }


        User user=userMapper.selectByPrimaryKey(userId);
        EnterUser enterUser=enterUserMapper.selectByPrimaryKey(userId);
        if (user==null&enterUser==null){
            return ServerResponse.createByErrorMessage("该用户id不存在");
        }

        comment=new Comment();
        comment.setUserId(userId);
        comment.setProductId(productId);
        comment.setTypeId(typeId);
        comment.setOrderNo(orderNo);
        comment.setOrderitemId(orderItemId);
        comment.setStatus(0);
        comment.setStarLevel(starLevel);
        comment.setContent(content);

        int rowCount=commentMapper.insert(comment);

        Order newOrder=new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatusEnum.EVALUATE.getCode());
        orderMapper.updateByPrimaryKeySelective(newOrder);

        if (rowCount>0){
            return ServerResponse.createBySuccess("插入消息成功");
        }
        return ServerResponse.createByErrorMessage("插入消息失败");
    }

    public ServerResponse<List<CommentVo>> list(int productId){
        List<Comment> commentList=commentMapper.selectByProductId(productId);
        List<CommentVo> commentVoList=Lists.newArrayList();
        for (Comment comment:commentList){
            CommentVo commentVo=assembleCommentVo(comment);
            commentVoList.add(commentVo);
        }
        return ServerResponse.createBySuccess(commentVoList);

    }

    private CommentVo assembleCommentVo(Comment comment){
        CommentVo commentVo=new CommentVo();

        User user=userMapper.selectByPrimaryKey(comment.getUserId());
        EnterUser enterUser=enterUserMapper.selectByPrimaryKey(comment.getUserId());

        commentVo.setCommentId(comment.getCommentId());
        commentVo.setUserId(comment.getUserId());
        if(user!=null&enterUser==null){
            commentVo.setUserName(user.getUsername());
            commentVo.setHeadImg(user.getHeadImg());
        }else {
            commentVo.setUserName(enterUser.getEnterName());
            commentVo.setHeadImg(enterUser.getHeadImg());
        }

        commentVo.setStarLevel(comment.getStarLevel());
        commentVo.setProductId(comment.getProductId());
        commentVo.setTypeId(comment.getTypeId());
        if(comment.getTypeId()==-1){
            commentVo.setTypeName("暂无分类");
        }else{
            ProductModel productModel=productModelMapper.selectByPrimaryKey(comment.getTypeId());
            commentVo.setTypeName(productModel.getName());
        }
        commentVo.setContent(comment.getContent());
        if (comment.getNewContent()==null){
            commentVo.setHasNewContent(false);
        }else{
            commentVo.setHasNewContent(true);
            commentVo.setNewContent(comment.getNewContent());
        }
        commentVo.setCreateTime(comment.getCreateTime());
        commentVo.setUpdateTime(comment.getUpdateTime());
        return commentVo;
    }

    public ServerResponse addNewContent(int userId,int commentId,String newContent){
        Comment comment=commentMapper.selectByUserIdCommentId(userId,commentId);
        if (comment==null){
            return ServerResponse.createByErrorMessage("该评论不存在或不属于该用户！");
        }
        if(comment.getNewContent()!=null){
            return ServerResponse.createByErrorMessage("您已添加追评，请勿重复添加");
        }

        Comment updateComment=new Comment();
        updateComment.setCommentId(commentId);
        updateComment.setNewContent(newContent);
        updateComment.setUpdateTime(comment.getUpdateTime());
        int rowCount=commentMapper.updateByPrimaryKeySelective(updateComment);

        Order order=orderMapper.selectByUserIdAndOrderNo(userId,comment.getOrderNo());
        Order newOrder=new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(Const.OrderStatusEnum.HAVAEVALUATE.getCode());

        orderMapper.updateByPrimaryKeySelective(newOrder);

        if (rowCount>0){
            return ServerResponse.createBySuccess("添加追评成功");
        }
        return ServerResponse.createByErrorMessage("添加追评失败");

    }

}
