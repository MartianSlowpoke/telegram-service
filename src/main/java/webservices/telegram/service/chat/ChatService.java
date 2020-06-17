package webservices.telegram.service.chat;

import java.util.Collection;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;

public interface ChatService {

	public void add(Message message) throws ChatServiceException;

	public void add(Chat chat) throws ChatDAOException, ChatTypeUnsupportedException;

	public Collection<Chat> getChats(Long userId) throws ChatDAOException;

	public Collection<Message> getMessages(Long chatId) throws ChatServiceException;

	public Message getMessage(Long messageId) throws ChatServiceException;

	public void deleteMessage(Long messageId) throws ChatServiceException;

	public void deleteChat(Long chatId) throws ChatDAOException;

	public void setChatEventListener(ChatEventListener listener);

	public void setMessageEventListener(MessageEventListener listener);

}
