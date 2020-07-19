package poc.net.server;

import java.time.Duration;

public class PocServerConfig {

	private Duration responseDelay = Duration.ZERO;

	public Duration getResponseDelay() {
		return responseDelay;
	}

	public void setResponseDelay(Duration responseDelay) {
		this.responseDelay = responseDelay;
	}

}
