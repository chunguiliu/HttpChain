package com.gcl.http;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;

import com.gcl.util.Invoker;
import com.gcl.util.ListUtil;
import com.gcl.util.MapUtil;

public class HttpUriRequestBuilder {

	public static HttpUriRequest builde(String method, String url, List<NameValuePair> paras, String charset) {
		if ("GET".equalsIgnoreCase(method)) {
			String getUrl = url;
			if (ListUtil.isNotEmpty(paras)) {
				getUrl += "?" + URLEncodedUtils.format(paras, charset);
			}
			return new HttpGet(getUrl);
		} else if ("POST".equalsIgnoreCase(method)) {
			HttpPost httpost = new HttpPost(url);
			if (ListUtil.isNotEmpty(paras)) {
				try {
					httpost.setEntity(new UrlEncodedFormEntity(paras, charset));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return httpost;
		}
		throw new IllegalArgumentException("not support " + method + " method!");
	}
	
	public static Map<String, String> iterateInvokeMap(Map<String, String> map){
		if(MapUtil.isNotEmpty(map)){
			Invoker invoker = new Invoker();
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
}
