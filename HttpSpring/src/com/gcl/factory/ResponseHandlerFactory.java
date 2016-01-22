package com.gcl.factory;


import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;

import com.gcl.dao.IHandlerCreater;
import com.gcl.dao.impl.HtmlHandlerCreater;
import com.gcl.dao.impl.ImageHandlerCreater;
import com.gcl.dao.impl.StreaHandlerCreater;
import com.gcl.util.LogUtil;

public class ResponseHandlerFactory {

	private static ArrayList<IHandlerCreater> createrList = new ArrayList<IHandlerCreater>();
	/**
	 * �Ƿ��ѽ��г�ʼ����
	 * ��ʹ�þ�̬����飬�û�ע���IHandlerCreater���нϸߵ����ȼ�
	 */
	private static boolean isInit = false;
	private static Object lock = new Object();
	static {
	}
	public static ResponseHandler<?> createResponseHandler(String contentType){
		if (!isInit) {
			synchronized (lock) {
				if (!isInit) {
					createrList.add(new ImageHandlerCreater());
					createrList.add(new StreaHandlerCreater());
					createrList.add(new HtmlHandlerCreater());
				}
			}
		}
		ResponseHandler<?> handler = null;
		for(IHandlerCreater creater : createrList){
			if(creater.compareWith(contentType)){
				handler = creater.createHandler();
				break;
			}
		}
		LogUtil.debug("contentType="+contentType+",handler="+handler);
		return handler;
	}
	public static ArrayList<IHandlerCreater> getCreaterList() {
		return createrList;
	}
	public static void setCreaterList(ArrayList<IHandlerCreater> createrList) {
		ResponseHandlerFactory.createrList = createrList;
	}
	public static void addCreater(IHandlerCreater handler){
		createrList.add(handler);
	}
}
