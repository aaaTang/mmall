package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.ISearchWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/search_words/")
public class SearchWordsController {

    @Autowired
    private ISearchWordsService iSearchWordsService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(){
        return iSearchWordsService.getList();
    }
}
