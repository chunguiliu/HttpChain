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
		LogUtil.debug("验证码："+verifier);
		
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
		//检测到账号被锁定，无法登陆
		if(response.toString().indexOf("该账号操作异常（账号异常）！") != -1){
			httpRequest.stop();
		}
		//验证码不正确，重新开始登陆
		if(response.toString().indexOf("验证码输入不正确!") != -1){
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
