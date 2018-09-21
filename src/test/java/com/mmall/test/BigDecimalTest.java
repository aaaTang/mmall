package com.mmall.test;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.pojo.Message;
import com.mmall.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class BigDecimalTest {

    @Test
    public void test1(){

        String test="atang1";
        if (test.length()==5){
            test=test+String.valueOf(1);
            System.out.println(test);
        }else{
            String name=test.substring(0,5);
            int index=Integer.valueOf(test.substring(5))+1;
            test=name+String.valueOf(index);
            System.out.println(test);
        }
    }
}
