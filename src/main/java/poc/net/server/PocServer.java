package poc.net.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class PocServer {

	private Logger log = LoggerFactory.getLogger(PocServer.class);

	private EventLoopGroup parentGroup = null;

	private EventLoopGroup childGroup = null;

	private final int port;

	private PocServerConfig config = new PocServerConfig();

	public PocServer(int port) {
		this.port = port;
	}

	public void setConfig(PocServerConfig config) {
		this.config = config;
	}

	public void start() throws InterruptedException {
		parentGroup = new NioEventLoopGroup(1);
		childGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(parentGroup, childGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new PocServerInitializer(config));

		// Bind and start to accept incoming connections.
		b.bind(port).sync();
	}

	public void stop() throws InterruptedException {
		if (parentGroup != null) {
			log.info("Shutting down parent thread group");
			parentGroup.shutdownGracefully().sync();
			log.info("Parent thread group shut down");
			parentGroup = null;
		}
		if (childGroup != null) {
			log.info("Shutting down child thread group");
			childGroup.shutdownGracefully().sync();
			log.info("Child thread group shut down");
			childGroup = null;
		}
	}

}
