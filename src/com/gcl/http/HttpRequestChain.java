package com.gcl.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.gcl.exception.ReRequestException;
import com.gcl.exception.RollBackException;
import com.gcl.exception.StopChainException;
import com.gcl.util.LogUtil;

public class HttpRequestChain {

	BasicHttpRequest[] requestList = {};
	
	int index = 0;
	private DefaultHttpClient httpClient;
	private HandleNameValues globalHandleNameValues;
	private int requestTimeOut = 120*1000;
	/**
	 * 通过配置文件和id返回指定的请求链
	 * @param config
	 * @param beanId
	 * @return
	 */
	public static HttpRequestChain getHttpRequestChain(String config, String beanId){
		ApplicationContext ac=new FileSystemXmlApplicationContext(config);
		HttpRequestChain chain = (HttpRequestChain) ac.getBean(beanId);
		return chain;
	}

	public void doRequest() throws Exception{
		if(httpClient == null){
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
			httpClient.setRedirectStrategy(new LaxRedirectStrategy());
			httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
			LogUtil.debug("设置超时时间为:"+requestTimeOut+"毫秒");
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, requestTimeOut);//连接时间
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestTimeOut);//数据传输时间
		}
		this.doRequest(httpClient);
	}
	public void doRequest(HttpClient httpClient) throws Exception {
		if(index < requestList.length && index >= 0){
			try {
				requestList[index++].request(httpClient, this);
			} catch (RollBackException e) {
				LogUtil.error("执行第"+index+"个请求时，回滚至"+e.getRollTo()+",异常："+e.getMessage());
				clearRollbackRequest(e.getRollTo());//清除原来产生的参数和请求头
				index = e.getRollTo() - 1;
				doRequest(httpClient);
			} catch (StopChainException e) {
				LogUtil.error("执行第"+index+"个请求停止,异常："+e.getMessage());
			} catch (ReRequestException e){
				LogUtil.error("重新执行第"+index+"个请求："+e.getMessage());
				clearRollbackRequest(index);//清除原来产生的参数和请求头
				index--;
				doRequest(httpClient);
			} catch (Exception e){
				LogUtil.error("出现异常："+e.getMessage()+",请求链终止", e);
				throw e;
			}
		}else if(index < 0){
			throw new ArrayIndexOutOfBoundsException("重新执行的下标从1开始，下标是否错误？");
		}
	}

	private void clearRollbackRequest(int rollTo){
		for(int i=rollTo-1; i<index; i++){
			requestList[i].refresh();
		}
	}
	public void refresh() throws Exception{
		this.setIndex(0);
		this.doRequest();
	}
	
	public void stop(){
		this.setIndex(requestList.length);
	}
	
	public BasicHttpRequest nextRequest(){
		if(hasNextRequest()){
			return requestList[index++];
		}
		return null;
	}
	public boolean hasNextRequest(){
		return index < requestList.length;
	}
	
	public BasicHttpRequest[] getRequestList() {
		return requestList;
	}
	
	public void setRequestList(BasicHttpRequest[] requestList) {
		this.requestList = requestList;
	}
	/**
	 * @return the httpClient
	 */
	public HttpClient getHttpClient() {
		return httpClient;
	}
	/**
	 * @param httpClient the httpClient to set
	 */
	public void setHttpClient(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public void addRequest(BasicHttpRequest httpRequest) {
		synchronized (requestList) {
			if (requestList == null)
				requestList = new BasicHttpRequest[1];
			else {
				BasicHttpRequest[] tmp = new BasicHttpRequest[requestList.length + 1];
				System.arraycopy(requestList, 0, tmp, 0, requestList.length);
				requestList = tmp;
			}
		}
		requestList[requestList.length - 1] = httpRequest;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public HandleNameValues getGlobalHandleNameValues() {
		return globalHandleNameValues;
	}
	public void setGlobalHandleNameValues(HandleNameValues globalHandleNameValues) {
		this.globalHandleNameValues = globalHandleNameValues;
	}
	public int getRequestTimeOut() {
		return requestTimeOut;
	}
	public void setRequestTimeOut(int requestTimeOut) {
		this.requestTimeOut = requestTimeOut;
	}
}
