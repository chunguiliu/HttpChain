package com.gcl.util;

public class Assert {

	/**
	 * 
	 * @param obj ���Ϊ�գ����׳�e�쳣
	 * @param msg
	 */
	public static void isNotNull(Object obj, String msg){
		if(obj == null)
			throw new IllegalArgumentException(msg);
	}
}
