package com.gcl.http;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public interface Request {

	/**
	 * ����һ���������л�ȡ��һ������Ĳ���
	 * @param httpResponse
	 */
	public List<NameValuePair> getRequestPara();
	
	public String getRequestUrl();
	
	
}
