package com.mmall.test;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.pojo.Message;
import com.mmall.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.IOException;
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

    @Test
    public void test2() throws IOException {

        String test="6IGU5oOz77yIVGhpbmtDZW50cmXvvIk=";

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = decoder.decodeBuffer(test);

        String str = new String(b,"utf-8");
        System.out.println(str);

    }

    @Test
    public void test9(){
        String test="1539108012445";
        Long te=Long.valueOf(test);
        System.out.println(te);
    }


}
