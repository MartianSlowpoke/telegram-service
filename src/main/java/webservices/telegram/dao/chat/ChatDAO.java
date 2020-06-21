package webservices.telegram.dao.chat;

import java.util.Collection;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.user.User;

public interface ChatDAO {

	public void addChat(Chat chat) throws ChatDAOException, ChatTypeUnsupportedException;

	public void addMessage(Message message) throws ChatDAOException;

	public void addParticipant(Chat chat, User user) throws ChatDAOException;

	public void removeParticipant(Chat chat, User user) throws ChatDAOException;

	public Chat getChat(Long chatId) throws ChatDAOException;

	public Message getMessage(Long messageId) throws ChatDAOException;

	public Collection<Chat> getChats(Long participiantId) throws ChatDAOException;

	public Collection<Message> getChatMessages(Long chatId) throws ChatDAOException;

	public Collection<User> getParticipiants(Long chatId) throws ChatDAOException;

	public void deleteChat(Long chatId) throws ChatDAOException;

}
