package poc.net;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ShareableHandlers {

	public static final StringEncoder STRING_ENCODER = new StringEncoder(StandardCharsets.UTF_8);

	public static final StringDecoder STRING_DECODER = new StringDecoder(StandardCharsets.UTF_8);

	private ShareableHandlers() {
	}

}
