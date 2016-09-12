package com.gcl.http.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 请求过程中的上下文环境
 * @author cg
 *
 */
public class HttpContext {
	
	private static ThreadLocal<Map<String, Object>> threadContext = new ThreadLocal<Map<String, Object>>() {
		protected synchronized Map<String, Object> initialValue() {
			Map<String, Object> map = new HashMap<String, Object>();
			return map;
		}
	};

	private static Map<String, Object> getMap() {
		return threadContext.get();
	}

	public static void addAttribute(String name, Object value) {
		getMap().put(name, value);
	}

	public static Object getAttribute(String name) {
		return getMap().get(name);
	}
	
	public static String getString(String name){
		return (String) getAttribute(name);
	}
	
	public static String getString(String name, String defaultValue){
		return StringUtils.defaultString(getString(name), defaultValue);
	}
}
