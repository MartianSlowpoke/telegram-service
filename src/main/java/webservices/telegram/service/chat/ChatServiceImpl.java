package webservices.telegram.service.chat;

import java.time.Instant;
import java.util.Collection;

import webservices.telegram.dao.chat.ChatDAO;
import webservices.telegram.dao.user.UserDAO;
import webservices.telegram.exception.UserDaoException;
import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.exception.user.UserNotFoundException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.user.User;
import webservices.telegram.websocket.UserEventListener;
import webservices.telegram.websocket.UserStateConnectionListener;

public class ChatServiceImpl implements ChatService, UserStateConnectionListener {

	private ChatDAO chatDAO;
	private UserDAO userDAO;
	private ChatEventListener chatHandler;
	private MessageEventListener messageHandler;
	private UserEventListener userHandler;

	public ChatServiceImpl(ChatDAO chatDAO, UserDAO userDAO) {
		super();
		this.chatDAO = chatDAO;
		this.userDAO = userDAO;
	}

	@Override
	public void add(Message message) throws ChatDAOException {
		message.setCreatedAt(Instant.now());
		message.setIsRead(false);
		chatDAO.addMessage(message);
		Chat chat = chatDAO.getChat(message.getChatId());
		MessageEvent event = new MessageEvent("NEW_MESSAGE", message, chat, Instant.now(),
				chatDAO.getParticipiants(message.getChatId()));
		notify(event);
	}

	@Override
	public void add(Chat chat) throws ChatDAOException, ChatTypeUnsupportedException {
		chat.setCreatedAt(Instant.now());
		chatDAO.addChat(chat);
		Collection<User> participants = chatDAO.getParticipiants(chat.getChatId());
		ChatEvent event = new ChatEvent("NEW_CHAT", chat, Instant.now(), participants);
		notify(event);
	}

	@Override
	public Collection<Chat> getChats(Long userId) throws ChatDAOException {
		Collection<Chat> chats = chatDAO.getChats(userId);
		return chats;
	}

	@Override
	public Collection<Message> getMessages(User initiator, Long chatId) throws ChatDAOException {
		Collection<Message> messages = chatDAO.getChatMessages(chatId);
		for (Message message : messages) {
			message.setIsRead(true);
		}
		Chat chat = chatDAO.getChat(chatId);
		for (User participant : chat.getParticipiants()) {
			Collection<Message> unreadMessages = chatDAO.getUnreadMessages(participant, chat);
			for (Message message : messages) {
				if (unreadMessages.contains(message)) {
					message.setIsRead(false);
				}
			}
		}
		return messages;
	}

	@Override
	public Message getMessage(Long messageId) throws ChatDAOException {
		Message message = chatDAO.getMessage(messageId);
		return message;
	}

	@Override
	public void deleteMessage(Long messageId) throws ChatDAOException {
		Message msg = chatDAO.getMessage(messageId);
		chatDAO.removeMessage(msg.getMessageId());
		MessageEvent event = new MessageEvent("DELETED_MESSAGE", msg, chatDAO.getChat(msg.getChatId()), Instant.now(),
				chatDAO.getParticipiants(msg.getChatId()));
		notify(event);
	}

	@Override
	public void deleteChat(Long chatId) throws ChatDAOException {
		Chat chat = chatDAO.getChat(chatId);
		ChatEvent event = new ChatEvent("DELETED_CHAT", chat, Instant.now(),
				chatDAO.getParticipiants(chat.getChatId()));
		chatDAO.deleteChat(chatId);
		notify(event);
	}

	private void notify(MessageEvent messageEvent) {
		if (messageHandler != null) {
			messageHandler.onEvent(messageEvent);
		}
	}

	private void notify(ChatEvent chatEvent) {
		if (chatHandler != null) {
			chatHandler.onEvent(chatEvent);
		}
	}

	private void notify(ParticipantEvent chatEvent) {
		if (chatHandler != null) {
			chatHandler.onEvent(chatEvent);
		}
	}

	@Override
	public void addParticipant(Long chatId, Long userId) throws ChatDAOException {
		try {
			Chat chat = chatDAO.getChat(chatId);
			User user = userDAO.get(userId);
			chatDAO.addParticipant(chat, user);
			ParticipantEvent event = new ParticipantEvent("NEW_PARTICIPANT", chat, user,
					chatDAO.getParticipiants(chat.getChatId()), Instant.now(),
					"a participant has joined to " + chat.getChatId() + " chat");
			notify(event);
		} catch (UserNotFoundException | UserDaoException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void removeParticipant(Long chatId, Long userId) throws ChatDAOException {
		try {
			Chat chat = chatDAO.getChat(chatId);
			User user = userDAO.get(userId);
			chatDAO.removeParticipant(chat, user);
			ParticipantEvent event = new ParticipantEvent("REMOVED_PARTICIPANT", chat, user,
					chatDAO.getParticipiants(chat.getChatId()), Instant.now(),
					"a participant has leaved " + chat.getChatId() + " chat");
			notify(event);
		} catch (UserNotFoundException | UserDaoException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public Chat getChat(Long chatId) throws ChatDAOException {
		return chatDAO.getChat(chatId);
	}

	@Override
	public void update(Chat chat) throws ChatDAOException {
		chatDAO.updateChat(chat);

	}

	@Override
	public void update(Message message) throws ChatDAOException {
		chatDAO.updateMessage(message);
		MessageEvent event = new MessageEvent("UPDATED_MESSAGE", message, chatDAO.getChat(message.getChatId()),
				Instant.now(), chatDAO.getParticipiants(message.getChatId()));
		notify(event);

	}

	@Override
	public void updateReadStatus(User participant, Message message) throws ChatDAOException {
		message.setIsRead(true);
		chatDAO.updateReadStatus(participant, message);
		MessageEvent event = new MessageEvent("READ_MESSAGE", chatDAO.getMessage(message.getMessageId()),
				chatDAO.getChat(message.getChatId()), Instant.now(), chatDAO.getParticipiants(message.getChatId()));
		notify(event);
	}

	@Override
	public void onOpen(User user) throws UserDaoException {
		user.setIsOnline(true);
		userDAO.updateOnlineStatus(user);
		UserEvent event = new UserEvent("ONLINE_USER", userDAO.get(user.getId()), Instant.now(),
				"the user has just connected to the server", userDAO.getAll());
		notify(event);
	}

	public void notify(UserEvent event) {
		if (userHandler != null) {
			userHandler.onEvent(event);
		}
	}

	@Override
	public void onClose(User user) throws UserDaoException {
		user.setIsOnline(false);
		user.setLastSeen(Instant.now());
		userDAO.updateOnlineStatus(user);
		UserEvent event = new UserEvent("ONLINE_USER", userDAO.get(user.getId()), Instant.now(),
				"the user has just connected to the server", userDAO.getAll());
		notify(event);
	}

	public ChatDAO getChatDAO() {
		return chatDAO;
	}

	public void setChatDAO(ChatDAO chatDAO) {
		this.chatDAO = chatDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public ChatEventListener getChatHandler() {
		return chatHandler;
	}

	public void setChatHandler(ChatEventListener chatHandler) {
		this.chatHandler = chatHandler;
	}

	public MessageEventListener getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageEventListener messageHandler) {
		this.messageHandler = messageHandler;
	}

	public UserEventListener getUserHandler() {
		return userHandler;
	}

	public void setUserHandler(UserEventListener userHandler) {
		this.userHandler = userHandler;
	}

}
