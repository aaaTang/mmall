package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DataTimeUtil {

    //joda-time

    //str->Date

    //Date->str

    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(formatStr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String formatStr){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static String getExpireTimeAt(String time,int hour){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date test=df.parse(time);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(test);
            rightNow.add(Calendar.HOUR,hour);
            test=rightNow.getTime();
            String expireTime=df.format(test);
            return expireTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int getExpireTime(String timestamp,long time){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date transTime= null;
        try {
            transTime = df.parse(timestamp);
            Date nowTime=new Date();
            long expireTime=(transTime.getTime()+time*3-nowTime.getTime())/1000;
            int redisTime=(int)expireTime;
            return redisTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //方法重写
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);

    }

}
