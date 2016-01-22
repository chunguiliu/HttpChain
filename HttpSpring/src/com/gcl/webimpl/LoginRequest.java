package com.gcl.webimpl;

import java.io.File;
import java.util.List;

import org.apache.http.NameValuePair;

import com.gcl.dao.AfterRequestHandler;
import com.gcl.dao.BeforeRequestHandler;
import com.gcl.http.BasicHttpRequest;
import com.gcl.http.ISessionAttr;
import com.gcl.util.LogUtil;

public class LoginRequest implements BeforeRequestHandler,AfterRequestHandler{

	private IRecognizeImage recImage;
	
	@Override
	public void beforeHandle(Object lastResponse, BasicHttpRequest httpRequest) {
		String verifier = recImage.read(lastResponse.toString());
		LogUtil.debug("��֤�룺"+verifier);
		
		httpRequest.addParameter("txtYZM", verifier);
		httpRequest.addParameter("txtUserName", (String) httpRequest.getAttribute("txtUserName"));
		httpRequest.addParameter("txtPwd", (String) httpRequest.getAttribute("txtPwd"));
		httpRequest.addParameters((List<NameValuePair>) httpRequest.getAttribute("loginParas"));
	}


	public String getTxtUserName(ISessionAttr sessionAttr) {
		return (String) sessionAttr.getAttribute("txtUserName");
	}

	public String getTxtPwd(ISessionAttr sessionAttr) {
		return (String) sessionAttr.getAttribute("txtPwd");
	}

	@Override
	public void afterHandle(Object response, BasicHttpRequest httpRequest) {
		LogUtil.debug(response);
		System.out.println(response);
		//��⵽�˺ű��������޷���½
		if(response.toString().indexOf("���˺Ų����쳣���˺��쳣����") != -1){
			httpRequest.stop();
		}
		//��֤�벻��ȷ�����¿�ʼ��½
		if(response.toString().indexOf("��֤�����벻��ȷ!") != -1){
			httpRequest.stop();
		}
	}


	public IRecognizeImage getRecImage() {
		return recImage;
	}


	public void setRecImage(IRecognizeImage recImage) {
		this.recImage = recImage;
	}

}
