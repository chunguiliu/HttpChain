package com.gcl.util;

import java.util.Map;

public class MapUtil {

	public static <K, V> boolean isEmpty(Map<K, V> map){
		return map == null || map.size() == 0;
	}
	public static <K, V> boolean isNotEmpty(Map<K, V> map){
		return !isEmpty(map);
	}
}
