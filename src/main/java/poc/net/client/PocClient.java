package poc.net.client;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import poc.net.Message;
import poc.net.Message.Type;
import poc.net.MessageMapper;
import poc.net.PocResponseTimeoutHandler;

public class PocClient {

	public static final int HEALTH_CHECK_INTERVAL_SEC = 4;
	public static final int REQUEST_TIMEOUT_SEC = 3;

	private Logger log = LoggerFactory.getLogger(PocClient.class);

	private EventLoopGroup loopGroup;

	private EventExecutorGroup execGroup;

	private Channel channel = null;

	private boolean healthCheckEnabled = true;

	private final PocClientContext clientContext = new PocClientContext();

	private final MessageMapper mapper = new MessageMapper();

	public void setHealthCheckEnabled(boolean healthCheckEnabled) {
		this.healthCheckEnabled = healthCheckEnabled;
	}

	public void connect(String host, int port) throws InterruptedException {
		loopGroup = new NioEventLoopGroup();
		execGroup = new DefaultEventExecutorGroup(2); // for the timeout tasks
		Bootstrap b = new Bootstrap();
		b.group(loopGroup);
		b.channel(NioSocketChannel.class);
		b.handler(new PocClientInitializer(clientContext));

		// Make the connection attempt.
		channel = b.connect(host, port).sync().channel();

		if (healthCheckEnabled) {
			log.info("Health check enabled, scheduling health check task");
			execGroup.scheduleWithFixedDelay(new HealthCheckTask(), HEALTH_CHECK_INTERVAL_SEC, HEALTH_CHECK_INTERVAL_SEC, TimeUnit.SECONDS);
		}
	}

	public Message send(Message message) throws InterruptedException {
		Type msgType = message.getType();
		String msgTypeStr = msgType.name();

		log.debug("Sending {} request to server", msgTypeStr);
		channel.writeAndFlush(mapper.toString(message)).sync();

		log.debug("Adding read timeout handler");
		channel.pipeline().addBefore(execGroup, PocClientInboundHandler.NAME, msgTypeStr, new PocResponseTimeoutHandler(msgType, REQUEST_TIMEOUT_SEC));

		log.debug("Waiting for response from server");
		Optional<Message> optionalRes = msgType.equals(Type.SYSTEM) ? clientContext.sysMsgQueue.take() : clientContext.bizMsgQueue.take();

		log.debug("Removing read timeout handler");
		channel.pipeline().remove(msgTypeStr);

		log.debug("Returning response to caller");
		return optionalRes.get();
	}

	public void stop() throws InterruptedException {
		if (execGroup != null) {
			log.info("Shutting down event execution group");
			execGroup.shutdownGracefully().sync();
			log.info("event execution group shut down");
			execGroup = null;
		}
		if (loopGroup != null) {
			log.info("Shutting down event loop group");
			loopGroup.shutdownGracefully().sync();
			log.info("Event loop group shut down");
			loopGroup = null;
		}
	}

	public class HealthCheckTask implements Runnable {

		private Logger log = LoggerFactory.getLogger(HealthCheckTask.class);

		@Override
		public void run() {
			Message req = new Message();
			req.setId("1");
			req.setType(Message.Type.SYSTEM);
			req.setSubtype(Message.Subtype.REQUEST);
			req.setContent("Ping");
			try {
				send(req);
			} catch (InterruptedException e) {
				log.warn("Health check task interrupted");
				Thread.currentThread().interrupt();
			}
		}

	}


}
