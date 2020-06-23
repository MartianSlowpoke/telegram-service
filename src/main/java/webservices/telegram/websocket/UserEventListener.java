package webservices.telegram.websocket;

import webservices.telegram.service.chat.UserEvent;

public interface UserEventListener {
	
	public void onEvent(UserEvent event);
	
}
