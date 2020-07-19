package poc.net.server;

import java.nio.charset.StandardCharsets;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import poc.net.Message;

public class PocServerInitializer extends ChannelInitializer<SocketChannel> {

	private final PocServerConfig config;

	public PocServerInitializer(PocServerConfig config) {
		this.config = config;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new FixedLengthFrameDecoder(Message.SIZE_IN_BYTES));
		p.addLast(new StringDecoder(StandardCharsets.UTF_8));
		p.addLast(new StringEncoder(StandardCharsets.UTF_8));
		p.addLast(new LoggingHandler("ServerLoggingHandler"));
		p.addLast(new PocServerInboundHandler(config));
	}

}
