package poc.net;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import poc.net.Message.Type;

public class PocReadTimeoutHandler extends IdleStateHandler {

	private final Logger log = LoggerFactory.getLogger(PocReadTimeoutHandler.class);

	private final Type type;

	/**
	 * Creates a new instance.
	 */
	public PocReadTimeoutHandler(Type timoutType, int timeoutSeconds) {
		super(timeoutSeconds, 0, 0, TimeUnit.SECONDS);
		this.type = timoutType;
	}

	@Override
	protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		log.info("channelIdle called for {}", type.name());
		assert evt.state() == IdleState.READER_IDLE;
		readTimedOut(ctx);
	}

	/**
	 * Is called when a read timeout was detected.
	 */
	protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
		ctx.fireExceptionCaught(new PocReadTimeoutException(type));
	}

}
