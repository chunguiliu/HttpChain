package com.gcl.http;

import java.util.Map;

import com.gcl.bean.HeaderList;
import com.gcl.bean.NameValueList;
/**
 * �������еĲ����������е�����ͷ�Ľӿ�
 * @author cg
 *
 */
public interface HandleNameValues {

	public void handleNameValuePairs(NameValueList nameValueList);
	public void handleHeaderMap(Map<String, String> headers);
}
