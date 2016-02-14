package com.gcl.util;

public class Assert {

	/**
	 * 
	 * @param obj 如果为空，则抛出e异常
	 * @param msg
	 */
	public static void isNotNull(Object obj, String msg){
		if(obj == null)
			throw new IllegalArgumentException(msg);
	}
}
