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
 * @version ����ʱ�䣺2013-9-3 ����03:18:53
 * �������վ��ʱ����վ�Ϸ��ص���Ϣ
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
	 * ��ȡ��ͼƬ�������浽ָ����·����
	 * @param httpMethodBase
	 * @param savePath ��������ͼƬ�ļ���·�������ļ���
	 * @return
	 */
	public static String getImage(HttpResponse httpResponse,String savePath){
		String filePath = savePath;//ͼƬ����·��
		File dir = new File(filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String filename="";//��֤��ͼƬ����,Ϊʱ�������3λ�����
		filename += new Date().getTime();
		Random ran = new Random();
		for(int i=0;i < 3;i++){
			filename += ran.nextInt(10);
		}
		filename += ".gif";//ͼƬ����
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
