package com.gcl.util;

import java.util.Collections;
import java.util.List;

public class ListUtil {

	public static boolean isEmpty(List<?> list){
		return list == null || list.size() == 0;
	}
	public static <T> List<T> emptyList(){
		return Collections.emptyList();
	}
	public static boolean isNotEmpty(List<?> list){
		return !isEmpty(list);
	}
}
