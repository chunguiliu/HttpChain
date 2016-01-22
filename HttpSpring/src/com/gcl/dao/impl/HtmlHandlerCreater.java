package com.gcl.dao.impl;

import com.gcl.dao.IHandlerCreater;
import com.gcl.dao.StringResponseHandler;

public class HtmlHandlerCreater implements IHandlerCreater {

	@Override
	public boolean compareWith(String contentType) {
		return contentType.indexOf("text/html") != -1;
	}

	@Override
	public StringResponseHandler createHandler() {
		return new HtmlResponseHandler();
	}

}
