package com.gcl.http;

public interface ISessionAttr {

	public void addAttribute(String name,Object value);
	public Object getAttribute(String name);
}
