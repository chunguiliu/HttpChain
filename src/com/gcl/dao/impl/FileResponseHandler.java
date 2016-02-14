package com.gcl.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.gcl.dao.FileSuffixHandler;
import com.gcl.dao.StringResponseHandler;

public abstract class FileResponseHandler implements StringResponseHandler,FileSuffixHandler {

	private String filePath;
	@Override
	public String handleResponse(HttpResponse arg0)
			throws ClientProtocolException, IOException {
		HttpEntity entity = arg0.getEntity();
		String filePath = this.getFilePath();//保存路径
		File dir = new File(filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String filename= String.valueOf(System.currentTimeMillis());//验证码图片名字,为时间戳加上3位随机数
		Random ran = new Random();
		for(int i=0;i < 3;i++){
			filename += ran.nextInt(10);
		}
		byte[] img = EntityUtils.toByteArray(entity);
		if (img == null)
			return (null);
		
		filename += "."+ this.getSuffix(entity.getContentType().getValue(), img);//从文件流中获取文件格式
		File storeFile = new File(filePath+"/"+filename);  
		FileOutputStream output = new FileOutputStream(storeFile);
		output.write(img);
		output.flush();
		output.close();
		return filePath+"/"+filename;
	}
	public String getFilePath() {
		if(filePath == null){
			setFilePath(System.getProperty("tmpFilePath") == null ? "./tmp/"
					: System.getProperty("tmpFilePath"));
		}
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public static void main(String[] args) throws IOException {
		InputStream in = null;
		 // 一次读多个字节
        byte[] tempbytes = new byte[3];
        int byteread = 0;
        in = new FileInputStream("D:\\data\\车管所网站\\orderResult\\20150629\\511124197510212417_1435507352365.html");
        // 读入多个字节到字节数组中，byteread为一次读入的字节数
        if((byteread = in.read(tempbytes)) != -1) {
            System.out.write(tempbytes, 0, byteread);
            System.out.println("======");
            System.out.println(new String(tempbytes).trim());
            System.out.println("======");
        }

	}
}
