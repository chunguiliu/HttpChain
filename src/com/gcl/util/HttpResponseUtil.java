package com.gcl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.util.EntityUtils;


/**
 * @author Liucg E-mail:hczclcg@163.com
 * @version 创建时间：2013-9-3 下午03:18:53
 * 处理访问站点时从网站上返回的信息
 */

public class HttpResponseUtil {

	
	public static String getHtml(HttpResponse httpResponse){
		try {
//			System.out.println("charset="+EntityUtils.getContentCharSet(httpResponse.getEntity()));
			return EntityUtils.toString(httpResponse.getEntity(), EntityUtils.getContentCharSet(httpResponse.getEntity()));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			HttpClientUtils.closeQuietly(httpResponse);
		}
		
		return "";
	}
	/**
	 * 读取到图片，并保存到指定的路径下
	 * @param httpMethodBase
	 * @param savePath 返回整个图片文件的路径加上文件名
	 * @return
	 */
	public static String getImage(HttpResponse httpResponse,String savePath){
		String filePath = savePath;//图片保存路径
		File dir = new File(filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String filename="";//验证码图片名字,为时间戳加上3位随机数
		filename += new Date().getTime();
		Random ran = new Random();
		for(int i=0;i < 3;i++){
			filename += ran.nextInt(10);
		}
		filename += ".gif";//图片类型
		File storeFile = new File(filePath+"/"+filename);  
		try {
			FileOutputStream output = new FileOutputStream(storeFile);
			byte[] img = EntityUtils.toByteArray(httpResponse.getEntity());
			if (img == null)
				return (null);
			output.write(img);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			HttpClientUtils.closeQuietly(httpResponse);
		}
		return filePath+"/"+filename;
	}
	
	
}
