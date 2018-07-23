package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.SearchWordsMapper;
import com.mmall.pojo.SearchWords;
import com.mmall.service.ISearchWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iSearchWordsService")
public class SearchWordsServiceImpl implements ISearchWordsService{

    @Autowired
    private SearchWordsMapper searchWordsMapper;

    public ServerResponse getList(){
        List<SearchWords> searchWordsList=searchWordsMapper.selectList();
        return ServerResponse.createBySuccess(searchWordsList);
    }
}
