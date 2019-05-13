package com.mmall.util.dzfp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Gzip {

	/**
	 * 
	 * <p>
	 * Description:使用gzip进行解压缩
	 * </p>
	 * 
	 * @param compressedStr
	 * @return
	 */
	public static String gunzip(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
			compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}
	
	
	public static String gunzip2(String compressedStr) {
		if (compressedStr == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
//			compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
			compressed = compressedStr.getBytes();
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
			byte[] ssString = new sun.misc.BASE64Decoder().decodeBuffer(decompressed);
			decompressed = ssString.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}
	
	public static void main(String[] args) {
		
		String Gzip1="H4sIAAAAAAAAAHWV33PaMAzH3/tX9Glvq5LwS3SQnYfSsQ51hrQ3ri9c1x5lbGvpdXdm/uv3NSGlMC8ciT+SIsuyrPQmRWm/XJTFbHZmp9Pp9Wx2UXzNj46Pe5Pi8mpyMfgiRZ7g6tErwU6vRVmaj0VeXg0GGNZWtTgYntnxeFQO8+H6fja6eQjOkqyTZmmrBa+1NliKDPOdJlCQfrZBnzSyJElb+Kdp1uC02w02lW4zy1fNOwl3W2m30Wkn7W47a3S4nWIGaIJFgef4R2NgnKkuaYb7Y7iVQVY8h+EHDCr9p7GYf69BPaB/r/uNwnnv1IpVF2Ni9o7ZiRiKsVMmUcfeGxdn55w3VryTOLO3nsQacTEmdmTJEVtnYwyJwt469RphIfJYjxNIXIxZFclU8ngzwsYjqVbgzDNFWATLYQOfxpoIGwoChCzKHONgbZUcJo6xeMuGmUiNiTI5S14RcJwNWYulElne7ucBCwt7VmudibEI9tFgzd6zxNiqIWwlEiIaY6xUmNTX/g4ZZYaYPGKu/e2xUeylIVQMq4+wwBVyQh71YmLsSSHDfipxhFFvhIpka+r877MgE4oZsV3VfAdsPIeKt1it0yhjKw3mExRsjNk5ayG1CDrCqFcyhDPJlmIcPGr1q+r1kIm9CbM5rtZ7yFgvwoGMq3gj7HEgHet/GecNp5njjH5BXhTprOffZ0hQ6wjnJf597veo2HbEM3s9ms1E81ZokzVUms3wddMNFnv6oea7RrwVVP16Ms6zJO28TZO3aXacJqfN7mnWCd0amm1Ln+bppn1PN4Lh+YdheV7kWat90oSi5sp4eF4WebNx0uqGVzYUFFbO8Nn6NCryHu3GL5qryShf/P69ej4lWj7Pb1bfbx5Pbh9I/Hyli9kdHvZuPlif3D2+n6/ufvVRYn9UzPriEkm/LNZvbn6t3s1XC2iW2tTLHy1o+hvp09PP50W/HC4epsl68W2JFwT/5e1al0UazLYhhSBCRINrZC5FojaDo/CdjH14/wLxkSDVkAcAAA==";		
		String gunzip = Gzip.gunzip(Gzip1);
		System.out.println(gunzip);
		
		
//		String strMw=ReadFromFile.readFileByLinesToString("D:/base64Pdf.txt");
//        String file=strMw.replaceAll("&#xd;", "");
//        //String pdfString=file.split("<PDF_FILE>")[1].split("</PDF_FILE>")[0];
//        String pdfString=strMw;
//        System.out.println(pdfString);
//    	FileOutputStream out=new FileOutputStream(new File("D:/电子发票/dzfppdf/"+"457"+"PDF.pdf"));
//    	Base64 base64=new Base64();
//		out.write(base64.decode(pdfString.getBytes("utf-8")));
	}
	
	
}
