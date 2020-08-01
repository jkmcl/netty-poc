package poc.net.client;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import poc.net.Message;

class PocClientContext {

	final BlockingQueue<Optional<Message>> sysMsgQueue = new SynchronousQueue<>();

	final BlockingQueue<Optional<Message>> bizMsgQueue = new SynchronousQueue<>();

}
