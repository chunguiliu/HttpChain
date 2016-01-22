package com.gcl.webimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class RecFromFile implements IRecognizeImage {

	@Override
	public String read(String filePath) {
		filePath = "./tmp/result.txt";
		Object lock = new Object();
		File resultFile = new File(filePath);
		long lastModify = resultFile.lastModified();
		do {
			try {
				synchronized (lock) {
					lock.wait(2000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (resultFile.lastModified() == lastModify);
		String verifier = read(new File(filePath));
		return verifier;
	}

	public static String read(File file){
	    BufferedReader br = null;
	    StringBuilder sb = new StringBuilder();
	    try{
	        br = new BufferedReader(new FileReader(file));
	        String s = null;
	        while((s = br.readLine())!=null){
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
