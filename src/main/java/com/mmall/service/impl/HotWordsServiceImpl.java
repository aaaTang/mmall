package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.HotWordsMapper;
import com.mmall.pojo.HotWords;
import com.mmall.service.IHotWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iHotWordsService")
public class HotWordsServiceImpl implements IHotWordsService {

    @Autowired
    private HotWordsMapper hotWordsMapper;

    public ServerResponse<List<HotWords>> list(){
        List<HotWords> hotWordsList= hotWordsMapper.selectList();
        if (hotWordsList==null){
            return ServerResponse.createByErrorMessage("热门关键词表为空");
        }
        return ServerResponse.createBySuccess(hotWordsList);
    }

    public ServerResponse add(HotWords hotWords) {
        int rowCount=hotWordsMapper.insert(hotWords);
        if (rowCount>0){
            return ServerResponse.createBySuccess("插入成功");
        }
        return ServerResponse.createByErrorMessage("插入失败");
    }
}
