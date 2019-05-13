package com.mmall.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Code {

    public static String Base64Decode(String encodeStr) {
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            byte[] b = decoder.decodeBuffer(encodeStr);
            String str = new String(b,"utf-8");
            return str;
        }catch (Exception e){
            return null;
        }
    }

    public static String Base64encode(String decodeStr) {
        BASE64Encoder encoder = new BASE64Encoder();
        try{
            byte[] textByte = decodeStr.getBytes("UTF-8");
            String encodedText = encoder.encode(textByte);
            return encodedText;
        }catch (Exception e){
            return null;
        }
    }

    public static String[] stringToArray(String subImage){
        String[] array=subImage.split(",");
        return array;
    }

}
