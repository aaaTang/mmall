package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotWords;

import java.util.List;

public interface IHotWordsService {

    ServerResponse<List<HotWords>> list();

    ServerResponse add(HotWords hotWords);
}
