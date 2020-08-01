package poc.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import poc.net.Message.Type;

public class PocResponseTimeoutHandler extends ReadTimeoutHandler {

	public static final String NAME = PocResponseTimeoutHandler.class.getName();

	private final Logger log = LoggerFactory.getLogger(PocResponseTimeoutHandler.class);

	private final Type type;

	/**
	 * Creates a new instance.
	 */
	public PocResponseTimeoutHandler(Type timoutType, int timeoutSeconds) {
		super(timeoutSeconds);
		this.type = timoutType;
	}

	@Override
	protected final void readTimedOut(ChannelHandlerContext ctx) throws Exception {
		log.debug("readTimedOut called for {}", type.name());
		ctx.fireExceptionCaught(new PocResponseTimeoutException(type));
	}

}
