package poc.net.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import poc.net.Message;
import poc.net.server.PocServer;
import poc.net.server.PocServerConfig;

@SpringBootTest
class PocClientTests {

	private Logger log = LoggerFactory.getLogger(PocClientTests.class);

	private static final Message createDummyRequest() {
		Message req = new Message();
		req.setId("1");
		req.setType(Message.Type.BUSINESS);
		req.setSubtype(Message.Subtype.REQUEST);
		req.setContent("Hello world");
		return req;
	}

	@Test
	void testNormal() throws Exception {
		log.info("Starting server");
		PocServer server = new PocServer(8080);
		server.start();

		PocClient client = new PocClient();

		log.info("Starting client");
		client.connect("0.0.0.0", 8080);

		log.info("Sending message to server");
		Message res = client.send(createDummyRequest());
		log.info("Received response from server: {}", res.getContent());

		assertEquals(Message.Subtype.RESPONSE, res.getSubtype());

		log.info("Stopping client");
		client.stop();

		log.info("Stopping server");
		server.stop();
	}

	@Test
	void testHealthCheck() throws Exception {
		PocServer server = new PocServer(8080);
		server.start();

		PocClient client = new PocClient();
		client.connect("0.0.0.0", 8080);

		log.info("Do nothing and wait for health checks to occur");
		Thread.sleep(Duration.ofSeconds(10).toMillis());

		log.info("Stopping client");
		client.stop();

		log.info("Stopping server");
		server.stop();
	}

	@Test
	void testHealthCheckTimeout() throws Exception {
		PocServer server = new PocServer(8080);
		PocServerConfig config = new PocServerConfig();
		config.setResponseDelay(Duration.ofSeconds(PocClient.REQUEST_TIMEOUT_SEC));
		server.setConfig(config);
		server.start();

		PocClient client = new PocClient();
		client.connect("0.0.0.0", 8080);

		log.info("Do nothing and wait for health check timeout to occur");
		Thread.sleep(Duration.ofSeconds((PocClient.HEALTH_CHECK_INTERVAL_SEC + PocClient.REQUEST_TIMEOUT_SEC) * 2 + 2).toMillis());

		log.info("Stopping client");
		client.stop();

		log.info("Stopping server");
		server.stop();
	}

}
