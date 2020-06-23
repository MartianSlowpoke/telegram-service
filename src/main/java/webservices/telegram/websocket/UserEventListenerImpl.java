package webservices.telegram.websocket;

import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import webservices.telegram.converter.UserJsonMapper;
import webservices.telegram.model.user.User;
import webservices.telegram.service.chat.UserEvent;

public class UserEventListenerImpl implements UserEventListener {

	private WebSocketServer ws;

	public UserEventListenerImpl(WebSocketServer ws) {
		super();
		this.ws = ws;
	}

	@Override
	public void onEvent(UserEvent event) {
		String payload = UserJsonMapper.eventToJson(event);
		for (User user : event.getRecipients()) {
			send(payload, user);
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
