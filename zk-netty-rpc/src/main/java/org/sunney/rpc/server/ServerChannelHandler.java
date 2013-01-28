package org.sunney.rpc.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunney.rpc.bean.RpcRequest;
import org.sunney.rpc.bean.RpcResponse;
import org.sunney.rpc.common.server.BeanRegister;
import org.sunney.rpc.common.server.Tuple;

/**
 * 服务器端处理器
 * 
 * @author sunney
 * 
 */
public class ServerChannelHandler extends ChannelInboundMessageHandlerAdapter<RpcRequest> {
	private BeanRegister beanRegister;
	private ExecutorService threadPool;
	private final static Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);

	public ServerChannelHandler(BeanRegister beanRegister, ExecutorService threadPool) {
		if(threadPool == null){
			throw new NullPointerException("目前线程池不能为空！");
		}
		this.beanRegister = beanRegister;
		this.threadPool = threadPool;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, RpcRequest message) throws Exception {
		try {
			if (message != null) {
				/**
				 * 在这里增加个线程池处理业务可以让Netty的worker线程尽快返回，以便处理其他客户端请求，
				 * 这样实际对服务器端的TPS没多大影响(硬件不变)，但是个人认为这样可以提高服务器端的吞吐量！
				 */
				threadPool.execute(new DoHandler(ctx, message, beanRegister));
			}
		} catch (Exception e) {
			logger.error("线程调度过于繁忙，导致处理请求失败！", e);
		}
	}

	private static class DoHandler implements Runnable {
		private ChannelHandlerContext ctx = null;
		private RpcRequest message = null;
		private BeanRegister beanRegister;

		public DoHandler(ChannelHandlerContext ctx, RpcRequest message, BeanRegister beanRegister) {
			this.ctx = ctx;
			this.message = message;
			this.beanRegister = beanRegister;
		}

		@Override
		public void run() {
			try {
				Tuple<Object, Method> tuple = beanRegister.lookup(message.getBeanName(), message.getMethodKey());
				RpcResponse response = new RpcResponse();
				if (tuple != null && tuple.getBean() != null && tuple.getMethod() != null) {
					Method method = tuple.getMethod();
					Object value = method.invoke(tuple.getBean(), message.getArgs());
					response.setResponse(value);
				} else {
					response.setResponse(null);
				}
				tuple = null;
				message = null;
//				ctx.channel().isActive();
				ChannelFuture future = ctx.write(response);
				ctx.flush();
				future.addListener(ChannelFutureListener.CLOSE);
			} catch (Exception e) {
				logger.error("业务调用失败！", e);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
