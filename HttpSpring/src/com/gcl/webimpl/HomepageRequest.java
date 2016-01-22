package com.gcl.webimpl;

import org.htmlparser.Node;
import org.htmlparser.tags.InputTag;

import com.gcl.bean.NameValueList;
import com.gcl.dao.AfterRequestHandler;
import com.gcl.http.BasicHttpRequest;
import com.gcl.util.HtmlParser;

public class HomepageRequest implements AfterRequestHandler {

	@Override
	public void afterHandle(Object response, BasicHttpRequest httpRequest) {
		Node[] nodes = HtmlParser.nodeArray(response.toString(), InputTag.class);
		NameValueList nameValues = new NameValueList();
		for(Node node : nodes){
			InputTag input = (InputTag)node;
			if(input.getAttribute("type").equals("hidden")){
				nameValues.add(input.getAttribute("name"), input.getAttribute("value"));
			}
		}
		httpRequest.addAttribute("loginParas", nameValues);
	}
}
