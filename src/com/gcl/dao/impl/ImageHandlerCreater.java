package com.gcl.dao.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gcl.dao.IHandlerCreater;
import com.gcl.dao.StringResponseHandler;

public class ImageHandlerCreater implements IHandlerCreater {

	@Override
	public boolean compareWith(String contentType) {
		return Pattern.matches("image/(\\w+)", contentType);
	}

	@Override
	public StringResponseHandler createHandler() {
		FileResponseHandler handler = new FileResponseHandler(){
			@Override
			public String getSuffix(String contentType, byte[] bytes) {
				Matcher m = Pattern.compile("image/(\\w+)").matcher(contentType);
				if(m.matches()){
					return m.group(1);
				}
				return null;
			}
		};
		return handler;
	}

}
