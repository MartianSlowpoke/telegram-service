package webservices.telegram.service.chat;

import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import webservices.telegram.converter.MessageJsonMapper;
import webservices.telegram.model.user.User;

public class MessageEventListenerImpl implements MessageEventListener {

	private WebSocketServer ws;

	public MessageEventListenerImpl(WebSocketServer ws) {
		this.ws = ws;
	}

	@Override
	public void onEvent(MessageEvent event) {
		String json = MessageJsonMapper.eventToJson(event).toJSONString();
		for (User recipient : event.getRecipients()) {
			send(json, recipient);
		}
	}

	private void send(String payload, User receiver) {
		Collection<WebSocket> clients = ws.getConnections();
		for (WebSocket w : clients) {
			User attached = w.getAttachment();
			if (attached.equals(receiver)) {
				w.send(payload);
			}
		}
	}

}
