package com.gcl.dao.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.gcl.dao.StringResponseHandler;

/**
 * ½«response×ª»»Îªhtml×Ö·û´®
 * @author cg
 *
 */
public class HtmlResponseHandler implements StringResponseHandler {

	@SuppressWarnings("deprecation")
	@Override
	public String handleResponse(HttpResponse arg0)
			throws ClientProtocolException, IOException {
		HttpEntity httpEntity = arg0.getEntity();
		return EntityUtils.toString(httpEntity, EntityUtils.getContentCharSet(httpEntity));
	}

}
