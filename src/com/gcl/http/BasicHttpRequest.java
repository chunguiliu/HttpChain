package com.gcl.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import com.gcl.bean.HeaderList;
import com.gcl.bean.NameValueList;
import com.gcl.dao.AfterRequestHandler;
import com.gcl.dao.BeforeRequestHandler;
import com.gcl.exception.StopChainException;
import com.gcl.factory.ResponseHandlerFactory;
import com.gcl.http.context.HttpContext;
import com.gcl.util.Invoker;
import com.gcl.util.ListUtil;
import com.gcl.util.LogUtil;
import com.gcl.util.MapUtil;

public class BasicHttpRequest {

	/* 外部注入 */
	protected String requestUrl;
	protected Map<String, String> paraMap;
	protected Map<String, String> headers = new HashMap<String, String>();
	protected String responseHandler;
	protected String method = "GET";
	protected BeforeRequestHandler beforeHandler;
	protected AfterRequestHandler afterHandler;
	/* 外部注入 end */
	
	protected NameValueList paraList = new NameValueList();
	protected HeaderList headerList = new HeaderList();
	protected HttpRequestChain requestChain;
	protected Invoker invoker = new Invoker();
	protected HandleNameValues handleNameValues;
	
	
	public BasicHttpRequest() {
		super();
	}

	public BasicHttpRequest(String method) {
		super();
		this.method = method;
	}

	public BasicHttpRequest(BeforeRequestHandler beforeHandler) {
		super();
		this.beforeHandler = beforeHandler;
	}

	public BasicHttpRequest(AfterRequestHandler afterHandler) {
		super();
		this.afterHandler = afterHandler;
	}

	public BasicHttpRequest(BeforeRequestHandler beforeHandler,
			AfterRequestHandler afterHandler) {
		super();
		this.beforeHandler = beforeHandler;
		this.afterHandler = afterHandler;
	}

	public BasicHttpRequest(String method, BeforeRequestHandler beforeHandler,
			AfterRequestHandler afterHandler) {
		super();
		this.method = method;
		this.beforeHandler = beforeHandler;
		this.afterHandler = afterHandler;
	}

	public void addHeader(String name, String value){
		
		headerList.add(name, value);
	}
	public void addParameter(String name, String value){
		this.paraList.add(name, value);
	}
	public void addParameters(List<NameValuePair> list){
		this.paraList.addAll(list);
	}

	public void refresh(){
		this.headerList.clear();
		this.paraList.clear();
	}
	
	public void stop(){
		this.requestChain.stop();
	}
	
	private Map<String, String> iterateInvokeValues(Map<String, String> map){
		if(MapUtil.isNotEmpty(map)){
			Iterator<String> keys = map.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				if (invoker.compile(map.get(key))) {
					map.put(key, String.valueOf(invoker.invoke()));
				}
			}
		}
		return map;
	}
	private List<Header> mergeHeader(){
		if (HttpContext.getAttribute("Referer") != null && !headers.containsKey("Referer"))
			this.headers.put("Referer", (String) HttpContext.getAttribute("Referer"));
		getNameValuesHandle().handleHeaderMap(this.headers);
		HeaderList headerList = new HeaderList();
		headerList.addAll(this.iterateInvokeValues(this.headers));
		return headerList;
	}
	
	private List<NameValuePair> mergeParas(){
		paraList.addAll(this.iterateInvokeValues(this.paraMap));
		getNameValuesHandle().handleNameValuePairs(paraList);
		return paraList;
	}
	
	/**
	 * 创建一个处理所有参数和请求头的接口，本实例的接口优先级最高，其次为请求链，若都为空，返回一个空实现的接口
	 * @return
	 */
	private HandleNameValues getNameValuesHandle(){
		HandleNameValues handle = null;
		handle = (this.handleNameValues == null ? 
				this.requestChain.getGlobalHandleNameValues() :
					this.handleNameValues);
		if(handle == null){
			handle = new HandleNameValues(){
				@Override
				public void handleNameValuePairs(NameValueList nameValueList) {}
				@Override
				public void handleHeaderMap(Map<String, String> headers) {}
			};
		}
		return handle;
	}
	/**
	 * 根据上次请求结果获取的charSet
	 * @return
	 */
	public String getCharSet(){
		return (String) HttpContext.getAttribute("charSet") == null ? "utf-8"
				: (String) HttpContext.getAttribute("charSet");
	}
	public void request(HttpClient httpClient, HttpRequestChain chain) throws Exception{
		//before request
		this.requestChain = chain;
		if(beforeHandler != null){
			beforeHandler.beforeHandle(this.getReponse(), this);
		}
		String reqUrl = getRequestUrl();
		if(reqUrl == null){
			throw new StopChainException("请求地址为空，终止请求链.");
		}
		HttpUriRequest uriRequest = getUriRequest(reqUrl);
		setHeader(uriRequest, mergeHeader());
		LogUtil.debug("请求： ["+ reqUrl +"]");
		if (headers.size() > 0) {
			LogUtil.debug("请求头信息:" + headers);
		}
		if (ListUtil.isNotEmpty(paraList)) {
			LogUtil.debug("参数:" + paraList);
		}
		//request
		Object response = execute(httpClient, uriRequest);
		//after request
		//cache
		//缓存请求的页面内容
		HttpContext.addAttribute("uriRequest", uriRequest);
		String referer = uriRequest.getURI().toString();
		HttpContext.addAttribute("Referer", referer);
		if(afterHandler != null){
			afterHandler.afterHandle(response, this);
		}
		setResponse(response);
		LogUtil.debug("请求["+ reqUrl +"]结束\n");
		this.requestChain.doRequest(httpClient);
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
	private HttpUriRequest getUriRequest(String reqUrl){
		if(!reqUrl.startsWith("http")){
			reqUrl = jointUrl(reqUrl);
		}
		List<NameValuePair> paras = mergeParas();
		if("GET".equalsIgnoreCase(this.getMethod())){
			String getUrl = reqUrl;
			if(ListUtil.isNotEmpty(paras)){
				getUrl += "?"+URLEncodedUtils.format(paras, this.getCharSet());
			}
			return new HttpGet(getUrl);
		}else{
			HttpPost httpost = new HttpPost(reqUrl);
			if (ListUtil.isNotEmpty(paras)) {
				try {
					httpost.setEntity(new UrlEncodedFormEntity(paras, this
							.getCharSet()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return httpost;
		}
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
	public Object execute(HttpClient httpClient, HttpUriRequest uriRequest) throws InstantiationException{
		boolean isException = false;
		int trys=0;
		do {
			// 发送请求，返回响应
			try {
				isException = false;
				return httpClient.execute(uriRequest,
						createResponseHandler(httpClient, uriRequest));
				
			} catch (HttpHostConnectException e) {
				isException = true;
				LogUtil.error("连接异常:"+e.getMessage(), e);
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				isException = true;
				LogUtil.error("连接IO异常:"+e.getMessage(), e);
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
				obj = invoker.getClass(responseHandler).newInstance();
			} catch (IllegalAccessException e) {
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

	public BeforeRequestHandler getBeforeHandler() {
		return beforeHandler;
	}

	public void setBeforeHandler(BeforeRequestHandler beforeHandler) {
		this.beforeHandler = beforeHandler;
	}

	public AfterRequestHandler getAfterHandler() {
		return afterHandler;
	}

	public void setAfterHandler(AfterRequestHandler afterHandler) {
		this.afterHandler = afterHandler;
	}

	public Map<String, String> getHeaders() {
		return headers;
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

	public HandleNameValues getHandleNameValues() {
		return handleNameValues;
	}

	public void setHandleNameValues(HandleNameValues handleNameValues) {
		this.handleNameValues = handleNameValues;
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