package com.gcl.dao;

import com.gcl.exception.ReRequestException;
import com.gcl.exception.RollBackException;
import com.gcl.exception.StopChainException;
import com.gcl.http.BasicHttpRequest;

public interface AfterRequestHandler {

	public void afterHandle(Object response, BasicHttpRequest httpRequest) throws RollBackException,StopChainException,ReRequestException;
}
