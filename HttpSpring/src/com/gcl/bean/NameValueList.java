package com.gcl.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.gcl.util.MapUtil;

public class NameValueList extends ArrayList<NameValuePair> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2732311213455765186L;

	public void add(String name, String value){
		this.add(new BasicNameValuePair(name, value));
	}
	
	public void addAll(Map<String, String> map){
		if(MapUtil.isNotEmpty(map)){
			Iterator<String> keys = map.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				this.add(key, map.get(key));
			}
		}
	}
	
	public void addAll(NameValueList otherList){
		super.addAll(otherList);
	}
}
