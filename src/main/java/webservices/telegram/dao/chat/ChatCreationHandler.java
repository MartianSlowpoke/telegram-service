package webservices.telegram.dao.chat;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.model.chat.Chat;

public interface ChatCreationHandler {

	public void handle(Chat chat) throws ChatDAOException;

}
