package com.gcl.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

public interface RequestChain {

	
	public void init(String config);
	
	public void doRequest(HttpClient httpClient);
	
	public void doRequest(BasicHttpRequest httpRequest, HttpClient httpClient);
	
	public void destory();
	
	public BasicHttpRequest nextRequest();
	
	public boolean hasNextRequest();
	
	public void addRequest(BasicHttpRequest httpRequest);
}
