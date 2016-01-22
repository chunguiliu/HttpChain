package com.gcl.dao;

import org.apache.http.HttpEntity;

public interface FileSuffixHandler {

	public String getSuffix(String contentType, byte[] bytes);
}
