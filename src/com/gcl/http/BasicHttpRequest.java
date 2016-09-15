package com.gcl.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import com.gcl.exception.StopChainException;
import com.gcl.factory.ResponseHandlerFactory;
import com.gcl.http.cer.IgnoreCertificate;
import com.gcl.http.context.HttpContext;
import com.gcl.util.ListUtil;
import com.gcl.util.LogUtil;
import com.gcl.util.MapUtil;

public class BasicHttpRequest {

	/* 外部注入 */
	protected String requestUrl;
	protected Map<String, String> paraMap = new HashMap<String, String>();
	protected Map<String, String> headers = new HashMap<String, String>();
	protected String responseHandler;
	protected String method = "GET";
	/* 外部注入 end */
	
	public BasicHttpRequest() {
		super();
	}

	public BasicHttpRequest(String method) {
		super();
		this.method = method;
	}
	
	public void addParameter(String key, String value){
		this.paraMap.put(key, value);
	}
	/**
	 * 根据上次请求结果获取的charSet
	 * @return
	 */
	public String getCharSet(){
		return (String) HttpContext.getAttribute("charSet") == null ? "utf-8"
				: (String) HttpContext.getAttribute("charSet");
	}
	
	/* 由子类按需实现 */
	protected void init(){}
	protected void finish(HttpClient httpClient, HttpUriRequest uriRequest, Object response,HttpRequestChain chain) throws Exception{
		setResponse(response);
		HttpContext.addAttribute("uriRequest", uriRequest);
		String referer = uriRequest.getURI().toString();
		HttpContext.addAttribute("Referer", referer);
		if (chain != null) {
			chain.doRequest(httpClient);
		}
	}
	
	protected List<NameValuePair> getParameters(){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if(MapUtil.isNotEmpty(this.paraMap)){
			for(String key : this.paraMap.keySet()){
				list.add(new BasicNameValuePair(key, paraMap.get(key)));
			}
		}
		return list;
	}
	
	protected List<Header> getHeaders(){
		List<Header> list = new ArrayList<Header>();
		if(MapUtil.isNotEmpty(this.headers)){
			for(String key : this.headers.keySet()){
				list.add(new BasicHeader(key, headers.get(key)));
			}
		}
		return list;
	}
	
	
	public void request(HttpClient httpClient, HttpRequestChain chain) throws Exception{
		//before request
		init();
		String reqUrl = getRequestUrl();
		if(reqUrl == null){
			throw new StopChainException("请求地址为空，终止请求链.");
		}
		HttpUriRequest uriRequest = HttpUriRequestBuilder.builde(this.getMethod(), reqUrl, getParameters(), this.getCharSet());
		
		setHeader(uriRequest, getHeaders());
		LogUtil.debug("请求： ["+ reqUrl +"]");
		//request
		Object response = execute(httpClient, uriRequest);
		//after request
		//cache
		//缓存请求的页面内容
		LogUtil.debug("请求["+ reqUrl +"]结束\n");
		finish(httpClient, uriRequest, response, chain);
	}
	
	private String jointUrl(String simpleUrl){
		HttpUriRequest uriRequest = (HttpUriRequest) HttpContext.getAttribute("uriRequest");
		if(uriRequest != null){
			URI uri = uriRequest.getURI();
			simpleUrl = uri.getScheme()+"://"+uri.getAuthority()+(uri.getAuthority().endsWith("/")?"":"/")+simpleUrl;
			LogUtil.debug("complete url:"+simpleUrl);
		}
		return simpleUrl;
	}
	
	public void setHeader(HttpUriRequest uriRequest,List<Header> headers){
		if(ListUtil.isNotEmpty(headers)){
			for(Header header : headers){
				uriRequest.addHeader(header);
			}
		}
	}
	
	protected int retryTimes = 3;
	/**
	 * 出现异常后会重试
	 * @param httpClient
	 * @param uriRequest
	 * @return 
	 * @throws InstantiationException
	 */
	public Object execute(HttpClient httpClient, HttpUriRequest uriRequest) throws InstantiationException {
		boolean isException = false;
		if ("https".equalsIgnoreCase(uriRequest.getURI().getScheme())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("SSL");
				ctx.init(null, new TrustManager[] { new IgnoreCertificate() }, null);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			SSLSocketFactory sf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, sf);
			httpClient.getConnectionManager().getSchemeRegistry().register(sch);
		}
		int trys = 0;
		do {
			// 发送请求，返回响应
			try {
				isException = false;
				return httpClient.execute(uriRequest, createResponseHandler(httpClient, uriRequest));

			} catch (HttpHostConnectException e) {
				isException = true;
				LogUtil.error("连接异常:" + e.getMessage(), e);
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				isException = true;
				LogUtil.error("连接IO异常:" + e.getMessage(), e);
				e.printStackTrace();
			} 
		} while (isException && trys++ < this.retryTimes);

		return null;
	}
	
	private ResponseHandler<Object> createResponseHandler(HttpClient httpClient, HttpUriRequest uriRequest) throws InstantiationException{
		if(responseHandler == null || "".equals(responseHandler)){
			return new ResponseHandlerImpl();
		}else{//InstantiationException
			Object obj = null;
			try {
				obj = Class.forName(responseHandler).newInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(!(obj instanceof ResponseHandler)){
				throw new InstantiationException("responseHandler 必须为ResponseHandler<String>的实现类！");
			}
			return (ResponseHandler<Object>) obj;
		}
	}
	public void setResponse(Object response){
		HttpContext.addAttribute("response", response);
	}
	public Object getReponse(){
		return HttpContext.getAttribute("response");
	}
	
	public String getRequestUrl() {
		if(requestUrl != null && !requestUrl.startsWith("http")){
			requestUrl = jointUrl(requestUrl);
		}
		return requestUrl;
	}

	public void setRequestUrl(String url) {
		this.requestUrl = url;
	}

	public String getResponseHandler() {
		return responseHandler;
	}

	public void setResponseHandler(String responseHandler) {
		this.responseHandler = responseHandler;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<String, String> paraMap) {
		this.paraMap = paraMap;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	
}
class ResponseHandlerImpl implements ResponseHandler<Object>{


	@Override
	public Object handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		if(response != null){//字符编码格式
			HttpContext.addAttribute("charSet", EntityUtils.getContentCharSet(response.getEntity()));
		}
		ResponseHandler<?> handler = ResponseHandlerFactory.createResponseHandler(response.getEntity().getContentType().getValue());
		Assert.notNull(handler, "解析返回结果的接口为null !!!");
		return handler.handleResponse(response);
	}
	
}