package poc.net.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import poc.net.Message;
import poc.net.Message.Type;
import poc.net.MessageMapper;
import poc.net.PocReadTimeoutException;

public class PocServerInboundHandler extends ChannelInboundHandlerAdapter {

	private final Logger log = LoggerFactory.getLogger(PocServerInboundHandler.class);

	private final PocServerConfig config;

	public PocServerInboundHandler(PocServerConfig config) {
		this.config = config;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    try {
			MessageMapper mapper = new MessageMapper();
			Message req = mapper.fromString((String) msg);

			Type msgType = req.getType();
			String msgTypeStr = msgType.name();

	    	long delayMillis = config.getResponseDelay().toMillis();
	    	if (delayMillis != 0) {
		    	log.info("Delaying {} response for {} ms", msgTypeStr, delayMillis);
	    		Thread.sleep(delayMillis);
	    	}

	    	log.info("Sending {} response back...", msgTypeStr);
			Message res = new Message();
			res.setId(req.getId());
			res.setType(req.getType());
			res.setSubtype(Message.Subtype.RESPONSE);
			res.setContent(req.getContent());
			ctx.writeAndFlush(mapper.toString(res));
	    } finally {
	        ReferenceCountUtil.release(msg);
	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof PocReadTimeoutException) {
			PocReadTimeoutException e = (PocReadTimeoutException) cause;
			log.info("{} timeout detected", e.getTimeoutType());
			return;
		}
		log.error("Closing connection due to exception", cause);
		ctx.close();
	}

}
