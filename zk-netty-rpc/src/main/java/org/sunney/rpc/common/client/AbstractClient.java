package org.sunney.rpc.common.client;

import java.util.concurrent.TimeoutException;

import org.sunney.rpc.bean.RpcRequest;
import org.sunney.rpc.bean.RpcResponse;
import org.sunney.rpc.common.common.TransportURL;

public abstract class AbstractClient implements Client {
	protected String host;
	protected int port;
	protected String beanName;

	private RpcResponse response;

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
	public void setResponse(RpcResponse response) {
		this.response = response;
	}

	@Override
	public RpcResponse getResponse() {
		if (this.response == null) {
			return null;
		}
		return this.response;
	}

	public Object invoke(RpcRequest request) {
		sendRequest(request);
		return getResponse().getResponse();
	}

	/**
	 * 已经废弃 性能太差
	 */
	@Override
	@Deprecated
	public void await(long timeout) throws TimeoutException {
		long currentMilli = System.currentTimeMillis();
		while (response == null) {
			if (System.currentTimeMillis() - currentMilli > timeout) {
				throw new TimeoutException("等待超时!");
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
