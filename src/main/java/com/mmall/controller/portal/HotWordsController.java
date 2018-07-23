package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.HotWords;
import com.mmall.service.IHotWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/hot_words/")
public class HotWordsController {

    @Autowired
    private IHotWordsService iHotWordsService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<List<HotWords>> list(){
        return iHotWordsService.list();
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HotWords hotWords){
        return iHotWordsService.add(hotWords);
    }

}
