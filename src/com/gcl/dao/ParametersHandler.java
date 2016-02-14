package com.gcl.dao;

import java.util.List;

import org.apache.http.NameValuePair;

public interface ParametersHandler {

	public void handle(List<NameValuePair> parameters);
}
