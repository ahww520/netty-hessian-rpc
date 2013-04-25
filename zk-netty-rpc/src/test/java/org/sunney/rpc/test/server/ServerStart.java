package org.sunney.rpc.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sunney.rpc.codec.RpcDecoder;
import org.sunney.rpc.codec.RpcEncoder;
import org.sunney.rpc.common.common.End;
import org.sunney.rpc.server.ServerChannelHandler;
import org.sunney.rpc.test.beanRegister.BeanRegisterImpl;

public class ServerStart {
	public static void main(String[] args) throws Exception {
		int port = 9991;
		final ExecutorService threadPool = Executors.newFixedThreadPool(100);

		ServerBootstrap b = new ServerBootstrap();
		try {
			b.group(new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 2), new NioEventLoopGroup(100))
			.channel(NioServerSocketChannel.class)
			.localAddress(new InetSocketAddress(port))
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.SO_LINGER, 1)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new LoggingHandler(LogLevel.INFO));

					// Add the number codec first,
					pipeline.addLast("encoder", new RpcEncoder(End.SERVER));
					pipeline.addLast("decoder", new RpcDecoder(End.SERVER));

					// and then business logic.
					// Please note we create a handler for every new channel
					// because it has stateful properties.
					pipeline.addLast("handler", new ServerChannelHandler(new BeanRegisterImpl(), threadPool));
				}
			});
			b.bind().sync().channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			b.shutdown();
		}
	}
}
