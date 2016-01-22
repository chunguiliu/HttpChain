package com.gcl.webimpl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gcl.http.HttpRequestChain;

public class Main {

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("request-driving.xml");
		HttpRequestChain driveOrderChain = (HttpRequestChain) applicationContext.getBean("homepagechain");
		driveOrderChain.addAttribute("txtUserName", "513722199312056039");
		driveOrderChain.addAttribute("txtPwd", "123456");
		driveOrderChain.doRequest();
	}

}
