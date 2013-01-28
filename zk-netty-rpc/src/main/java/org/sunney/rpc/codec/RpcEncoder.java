package org.sunney.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.sunney.rpc.bean.RpcMessage;
import org.sunney.rpc.common.common.End;
import org.sunney.rpc.common.common.codec.RpcProtocol;

/**
 * 编码器 ( 把请求信息序列化为字节数组并写入管道 )
 * 
 * @author sunney
 * 
 */
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
	private End end;

	public RpcEncoder(End end) {
		this.end = end;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, RpcMessage message, ByteBuf out) throws Exception {
		out.writeBytes(RpcProtocol.VERSION_1.encode(end, message));
	}

}
