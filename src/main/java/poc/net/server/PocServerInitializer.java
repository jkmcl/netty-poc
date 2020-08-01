package poc.net.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import poc.net.Message;
import poc.net.ShareableHandlers;

public class PocServerInitializer extends ChannelInitializer<SocketChannel> {

	private final PocServerConfig config;

	public PocServerInitializer(PocServerConfig config) {
		this.config = config;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new FixedLengthFrameDecoder(Message.SIZE_IN_BYTES));
		p.addLast(ShareableHandlers.STRING_DECODER);
		p.addLast(ShareableHandlers.STRING_ENCODER);
		p.addLast(new LoggingHandler("ServerLoggingHandler"));
		p.addLast(PocServerInboundHandler.NAME, new PocServerInboundHandler(config));
	}

}
