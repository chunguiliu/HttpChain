package com.gcl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.util.ParserException;

public class HtmlParser {

	public static <T> Node[] nodeArray(String html, Class<? extends T> cls){
		Parser parser = Parser.createParser(html, "utf-8");
		NodeFilter divFilter = new NodeClassFilter(cls);
		try {
			Node[] nodes = parser.extractAllNodesThatMatch(divFilter).toNodeArray();
			return nodes;
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String frameStr = "text/html; charset=utf-8";
		/*Node[] nodes = nodeArray(frameStr, "iframe", "utf-8");
		for(Node node:nodes){
			NodeParser n = new NodeParser(node.toHtml());
			System.out.println(n.getAttr("id"));
		}*/
		Pattern p = Pattern.compile("charset=([^;\\s]+)");
		Matcher m = p.matcher(frameStr); 
		if(m.find()){
			System.out.println(m.group());
		}
	}
	
	public static String getAttr(String html, String attr){
		Pattern p = Pattern.compile(attr+"=\"([^\"]*)\"");
		Matcher m = p.matcher(html); 
		if(m.find()){
			return m.group(1);
		}
		return "";
	}
	public static Node[] nodeArray(String html, String tagName, String charSet){
		Parser parser = Parser.createParser(html, charSet);
		NodeFilter divFilter = new TagNameFilter(tagName);
		try {
			Node[] nodes = parser.extractAllNodesThatMatch(divFilter).toNodeArray();
			return nodes;
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}

}
