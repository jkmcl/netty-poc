package poc.net;

import io.netty.channel.ChannelException;
import poc.net.Message.Type;

public class PocResponseTimeoutException extends ChannelException {

	private static final long serialVersionUID = 1L;

	private final Type type;

	PocResponseTimeoutException(Type type) {
		this.type = type;
	}

	public Type getTimeoutType() {
		return type;
	}

}
