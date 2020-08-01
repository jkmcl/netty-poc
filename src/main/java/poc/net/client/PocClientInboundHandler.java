package poc.net.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import poc.net.Message;
import poc.net.MessageMapper;
import poc.net.PocProtocolException;
import poc.net.PocResponseTimeoutException;

public class PocClientInboundHandler extends ChannelInboundHandlerAdapter {

	public static final String NAME = PocClientInboundHandler.class.getName();

	private final Logger log = LoggerFactory.getLogger(PocClientInboundHandler.class);

	private final MessageMapper mapper = new MessageMapper();

	private final PocClientContext clientContext;

	public PocClientInboundHandler(PocClientContext clientContext) {
		this.clientContext = clientContext;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    try {
	    	log.info("Putting response in queue...");
			Message res = mapper.fromString((String) msg);
			boolean offerResult;
			if (res.getType().equals(Message.Type.SYSTEM)) {
				offerResult = clientContext.sysMsgQueue.offer(Optional.of(res));
			} else {
				offerResult = clientContext.bizMsgQueue.offer(Optional.of(res));
			}
			if (!offerResult ) {
				throw new PocProtocolException("Received unsolicited message: " + msg);
			}
	    } finally {
	        ReferenceCountUtil.release(msg);
	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof PocResponseTimeoutException) {
			PocResponseTimeoutException e = (PocResponseTimeoutException) cause;
			log.info("{} timeout detected", e.getTimeoutType().name());
			return;
		}
		log.error("Exception caught", cause);

		boolean offerResult;
		offerResult = clientContext.sysMsgQueue.offer(Optional.empty());
		if (offerResult) {
			log.info("Unblocked thread waiting for system response message");
		}
		offerResult = clientContext.bizMsgQueue.offer(Optional.empty());
		if (offerResult) {
			log.info("Unblocked thread waiting for business response message");
		}

		log.info("Closing connection", cause);
		ctx.close();
	}

}
