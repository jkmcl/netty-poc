package poc.net.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import poc.net.Message;

public class PocServerContext {

	private List<Message> messagesReceived = new CopyOnWriteArrayList<>();

	public List<Message> getMessagesReceived() {
		return messagesReceived;
	}

	public void setMessagesReceived(List<Message> messagesReceived) {
		this.messagesReceived = messagesReceived;
	}

}
