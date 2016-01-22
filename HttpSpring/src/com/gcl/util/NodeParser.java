package com.gcl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeParser {

	private String html;
	private Map<String, String> attrs = new HashMap<String, String>();

	public NodeParser(String html) {
		super();
		this.html = html;
		Pattern p = Pattern.compile("([\\w]+)=\"([^\"]*)\"");
		Matcher m = p.matcher(html); 
		while(m.find()){
			attrs.put(m.group(1), m.group(2));
		}
	}

	public String getAttr(String attr){
		return attrs.get(attr);
	}
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
}
