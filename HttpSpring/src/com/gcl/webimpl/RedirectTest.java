package com.gcl.webimpl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gcl.dao.AfterRequestHandler;
import com.gcl.http.BasicHttpRequest;
import com.gcl.http.HttpRequestChain;

public class RedirectTest implements AfterRequestHandler {

	@Override
	public void afterHandle(Object response, BasicHttpRequest httpRequest) {
		System.out.println(response);
	}

	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("request-test.xml");
		HttpRequestChain driveOrderChain = (HttpRequestChain) applicationContext.getBean("chain");
		driveOrderChain.doRequest();
	}

}
