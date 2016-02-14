package com.gcl.http;

import java.util.Map;

import com.gcl.bean.HeaderList;
import com.gcl.bean.NameValueList;
/**
 * 处理所有的参数或者所有的请求头的接口
 * @author cg
 *
 */
public interface HandleNameValues {

	public void handleNameValuePairs(NameValueList nameValueList);
	public void handleHeaderMap(Map<String, String> headers);
}
