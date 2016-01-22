package com.gcl.dao;

import org.apache.http.client.ResponseHandler;


public interface IHandlerCreater {

	public boolean compareWith(String contentType);
	public ResponseHandler<?> createHandler();
}
