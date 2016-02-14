package com.gcl.dao.impl;

import com.gcl.dao.IHandlerCreater;
import com.gcl.dao.StringResponseHandler;

public class StreaHandlerCreater implements IHandlerCreater {

	@Override
	public boolean compareWith(String contentType) {
		return contentType.indexOf("application/octet-strea") != -1;
	}

	@Override
	public StringResponseHandler createHandler() {
		FileResponseHandler handler = new FileResponseHandler(){
			@Override
			public String getSuffix(String contentType, byte[] bytes) {
				byte[] bs = new byte[3];
				System.arraycopy(bytes, 0, bs, 0, 3);
				return new String(bs);
			}
		};
		return handler;
	}

}
