package poc.net;

import io.netty.channel.ChannelException;
import poc.net.Message.Type;

public class PocReadTimeoutException extends ChannelException {

	private static final long serialVersionUID = 2145331647435273046L;

	private final Type type;

	PocReadTimeoutException(Type type) {
		this.type = type;
	}

	public Type getTimeoutType() {
		return type;
	}

}
