package com.gcl.http;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

public interface Request {

	/**
	 * 从上一次请求结果中获取这一次请求的参数
	 * @param httpResponse
	 */
	public List<NameValuePair> getRequestPara();
	
	public String getRequestUrl();
	
	
}
