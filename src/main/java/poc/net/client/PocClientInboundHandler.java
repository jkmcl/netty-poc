package poc.net.client;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import poc.net.Message;
import poc.net.MessageMapper;
import poc.net.PocReadTimeoutException;

public class PocClientInboundHandler extends ChannelInboundHandlerAdapter {

	private Logger log = LoggerFactory.getLogger(PocClientInboundHandler.class);

	private BlockingQueue<Message> sysQueue;

	private BlockingQueue<Message> bizQueue;

	public PocClientInboundHandler(BlockingQueue<Message> sysQueue, BlockingQueue<Message> bizQueue) {
		this.sysQueue = sysQueue;
		this.bizQueue = bizQueue;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    try {
	    	log.info("Putting response in queue...");
			MessageMapper mapper = new MessageMapper();
			Message res = mapper.fromString((String) msg);
			if (res.getType().equals(Message.Type.SYSTEM)) {
				sysQueue.add(res);
			} else {
				bizQueue.add(res);
			}
	    } finally {
	        ReferenceCountUtil.release(msg);
	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof PocReadTimeoutException) {
			PocReadTimeoutException e = (PocReadTimeoutException) cause;
			log.info("{} timeout detected", e.getTimeoutType().name());
			return;
		}
		log.error("Closing connection due to exception", cause);
		ctx.close();
	}

}
