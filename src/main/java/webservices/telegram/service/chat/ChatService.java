package webservices.telegram.service.chat;

import java.util.Collection;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.user.User;

public interface ChatService {

	public void add(Message message) throws ChatDAOException;

	public void add(Chat chat) throws ChatDAOException, ChatTypeUnsupportedException;

	public void update(Chat chat) throws ChatDAOException;

	public void update(Message message) throws ChatDAOException;

	public void updateReadStatus(User participant, Message message) throws ChatDAOException;

	public void addParticipant(Long chatId, Long userId) throws ChatDAOException;

	public Collection<Chat> getChats(Long userId) throws ChatDAOException;

	public Chat getChat(Long chatId) throws ChatDAOException;

	public Collection<Message> getMessages(User initiator, Long chatId) throws ChatDAOException;

	public Message getMessage(Long messageId) throws ChatDAOException;

	public void removeParticipant(Long chatId, Long userId) throws ChatDAOException;

	public void deleteMessage(Long messageId) throws ChatDAOException;

	public void deleteChat(Long chatId) throws ChatDAOException;

}
