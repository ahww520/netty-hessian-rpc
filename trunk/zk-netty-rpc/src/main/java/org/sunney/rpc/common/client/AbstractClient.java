package org.sunney.rpc.common.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.sunney.rpc.bean.RpcRequest;
import org.sunney.rpc.bean.RpcResponse;
import org.sunney.rpc.common.common.TransportURL;

public abstract class AbstractClient implements Client {
	private AtomicInteger rquestId = new AtomicInteger(0);
	protected String host;
	protected int port;
	protected String beanName;
	protected ConcurrentHashMap<Integer, ArrayBlockingQueue<RpcResponse>> responseMap = new ConcurrentHashMap<Integer, ArrayBlockingQueue<RpcResponse>>();

	// private RpcResponse response;

	public AbstractClient(TransportURL url) {
		this.host = url.getHost();
		this.port = url.getPort();
		this.beanName = url.getPath();
	}

	@Override
	public String getBeanName() {
		return this.beanName;
	}

	@Override
	public void setResponse(Integer requestIndex, RpcResponse response) {
		ArrayBlockingQueue<RpcResponse> responseQueue = responseMap.get(requestIndex);
		if(responseQueue == null){
			responseQueue = new ArrayBlockingQueue<RpcResponse>(1);
		}
		responseQueue.add(response);
		
		responseMap.putIfAbsent(requestIndex, responseQueue);
	}

	@Override
	public RpcResponse getResponse(Integer requestIndex) {
		ArrayBlockingQueue<RpcResponse> responseQueue = responseMap.get(requestIndex);
		if (responseQueue != null) {
			try {
				RpcResponse response = responseQueue.poll(30*1000,TimeUnit.MILLISECONDS);
				responseMap.remove(requestIndex);
				return response;
			} catch (InterruptedException e) {
			}
		}
		responseMap.remove(requestIndex);
		return null;
	}

	public Object invoke(RpcRequest request) {
		int rid = rquestId.getAndDecrement();
		request.setMagic(rid);
		sendRequest(request);
		return getResponse(rid).getResponse();
	}
}
