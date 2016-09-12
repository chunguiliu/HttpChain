package com.gcl.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.gcl.util.MapUtil;

public class HeaderList extends ArrayList<Header> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6215743546483540304L;

	public void add(String name, String value){
		this.add(new BasicHeader(name, value));
	}

	public void addAll(Map<String, String> map) {
		if (MapUtil.isNotEmpty(map)) {
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				this.add(key, map.get(key));
			}
		}
	}
}
