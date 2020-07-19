package poc.net.client;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import poc.net.Message;

public class PocClientInitializer extends ChannelInitializer<SocketChannel> {

	private BlockingQueue<Message> sysQueue;

	private BlockingQueue<Message> bizQueue;

	public PocClientInitializer(BlockingQueue<Message> sysQueue, BlockingQueue<Message> bizQueue) {
		this.sysQueue = sysQueue;
		this.bizQueue = bizQueue;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new FixedLengthFrameDecoder(Message.SIZE_IN_BYTES));
		p.addLast(new StringDecoder(StandardCharsets.UTF_8));
		p.addLast(new StringEncoder(StandardCharsets.UTF_8));
		p.addLast(new LoggingHandler("ClientLoggingHandler"));
		p.addLast(PocClientInboundHandler.class.getSimpleName(), new PocClientInboundHandler(sysQueue, bizQueue));
	}

}
