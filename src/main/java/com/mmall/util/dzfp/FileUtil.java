package com.mmall.util.dzfp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;

/**
 * 
 * @author zy
 * 功能 操作文件工具类
 * date 2018-08-16
 */
public class FileUtil {

	/**
	 * 
	 * @param filename   文件名
	 * @param filecontent文件流(字符类型)
	 * @param writepath  存放路径
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static Map<String,String>  SavePDF(String filename,String filecontent,String writepath) throws UnsupportedEncodingException,FileNotFoundException {
		Map<String,String> map = new HashMap<String, String>();
		File file =new File(writepath);    
		//如果文件夹不存在则创建    
		if  (!file .exists() && !file.isDirectory()){       
		    System.out.println("//目录不存在，创建目录");  
		    file.mkdir();    
		} else {  
		    System.out.println("//目录存在，不用创建目录。");  
		}
    	map.put("PDF_FILE", "");
    	map.put("PDF_URL", "");
    	String separator = File.separator;
    	writepath=writepath +separator+filename+".pdf";
    	System.out.println("write_path="+writepath);
		FileOutputStream out = new FileOutputStream(writepath);
		try {
			out.write(new BASE64Decoder().decodeBuffer(filecontent));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
		try {
			String str = "";
			FileUtil.SavePDF("22562695336392100", str, "F:\\");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
