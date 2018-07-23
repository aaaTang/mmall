package com.mmall.test;

import com.mmall.common.Const;
import com.mmall.pojo.Product;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

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
    public void test2(){
        String test="110-1.jpg,110-2.jpg,110-3.jpg,110-4.jpg";
        String[] array=test.split(",");
        System.out.println(array[0]);
    }

    @Test
    public void test4(){
        for(int i=130;i<262;i++){
            String test=i+"-1.jpg,"+i+"-2.jpg,"+i+"-3.jpg,"+i+"-4.jpg";
            System.out.println(test);
        }
    }

    @Test
    public void test3(){
        for (int i=1;i<11;i++){
            String temp=String.valueOf(i)+"-1.jpg";
            System.out.println(temp);
        }
    }

    @Test
    public void test5(){
        Random random = new Random();
        int randomIndex = random.nextInt(1);
        System.out.println(randomIndex);
    }

}
