package org.sunney.rpc.common.client;

import java.util.concurrent.TimeoutException;

import org.sunney.rpc.bean.RpcRequest;
import org.sunney.rpc.bean.RpcResponse;

/**
 * 客户端帮助类
 * 
 * @author sunney
 * 
 */
public interface Client {
	Object invoke(RpcRequest request);

	void sendRequest(RpcRequest request);

	void setResponse(RpcResponse response);

	RpcResponse getResponse();

	String getBeanName();

	void await(long timeout) throws TimeoutException;
}
