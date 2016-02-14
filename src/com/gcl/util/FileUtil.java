package com.gcl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	public static String read(String file){
		return read(new File(file));
	}
	public static String read(File file){
	    BufferedReader br = null;
	    StringBuilder sb = new StringBuilder();
	    try{
	        br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
	        String s = null;
	        while((s = br.readLine())!=null){//使用readLine方法，一次读一行
	            sb.append(s);
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally {
	    	try {
	    		if(br != null)
	    			br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}    
	    }
	    return sb.toString();
	}

}
