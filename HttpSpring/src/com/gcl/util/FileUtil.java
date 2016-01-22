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
	        br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
	        String s = null;
	        while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
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
