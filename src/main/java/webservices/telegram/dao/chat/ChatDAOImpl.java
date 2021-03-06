package webservices.telegram.dao.chat;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mysql.cj.jdbc.MysqlDataSource;

import webservices.telegram.dao.user.UserDAO;
import webservices.telegram.exception.ResourceNotFoundException;
import webservices.telegram.exception.UserDaoException;
import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.exception.user.UserNotFoundException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.ChatPhoto;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.chat.MessageFile;
import webservices.telegram.model.user.User;

public class ChatDAOImpl implements ChatDAO {

	protected final static String SQL_INSERT_PRIVATE_CHAT = "INSERT INTO chat (chat_type,fk_owner, created_at) VALUES (?,?,?);";
	protected final static String SQL_INSERT_PARTICIPIANT = "INSERT INTO participiant (fk_user, fk_chat, created_at) VALUES (?,?,?);";
	protected final String SQL_INSERT_GROUP_CHAT = "INSERT INTO chat (chat_type,name, description, fk_owner, fk_chat_photo) VALUES (?,?,?,?,?);";
	protected final String SQL_INSERT_MESSAGE = "INSERT INTO message (fk_chat, fk_sender,content,created_at, fkFile) VALUES (?,?,?,?,?);";
	protected final String SQL_INSERT_MESSAGE_FILE = "INSERT INTO messageFile (fileName,fileData) VALUES (?,?);";
	protected final String SQL_INSERT_PARTICIPANT = "INSERT INTO participiant (fk_user, fk_chat, created_at) VALUES (?,?,?);";
	protected final String SQL_REMOVE_PARTICIPANT = "DELETE FROM participiant WHERE fk_user = ? AND fk_chat = ?;";
	protected final String SQL_ADD_CHAT_LAST_MESSAGE = "UPDATE chat SET fk_last_message = ? WHERE chat_id = ?;";
	protected final String SQL_ADD_UNREAD_MESSAGE = "INSERT INTO unread_messages (fk_user, fk_message) VALUES (?,?);";

	protected final String SQL_GET_PARTICIPIANTS = "SELECT fk_user,fk_chat FROM participiant WHERE fk_chat = ?;";
	protected final String SQL_GET_CHATS_ID_OF_PARTICIPIANT = "SELECT fk_chat FROM participiant WHERE fk_user = ?;";
	protected final String SQL_GET_CHAT_BY_ID = "SELECT chat_id, chat_type, description, fk_owner,fk_last_message,created_at,fk_chat_photo  FROM chat WHERE chat_id = ?;";
	protected final String SQL_GET_CHAT_MESSAGES = "SELECT message_id FROM message WHERE fk_chat = ?;";
	protected final String SQL_GET_MESSAGE_BY_ID = "SELECT message_id,fk_chat,fk_sender,content,fkFile,created_at FROM message WHERE message_id = ?";
	protected final String SQL_GET_MESSAGE_FILE = "SELECT fileName,fileData FROM messageFile WHERE fileId = ?;";
	protected final String SQL_GET_LAST_MESSAGE_ACCESSOR = "SELECT message_id FROM message WHERE message_id < ? AND fk_chat = ? LIMIT 1;";
	protected final String SQL_GET_UNREAD_MESSAGES = "SELECT u.id, u.fk_user, u.fk_message, m.fk_chat FROM unread_messages u INNER JOIN message m ON u.fk_message = m.message_id WHERE m.fk_chat = ? AND u.fk_user = ?;";

	protected final String SQL_DELETE_PARTICIPANT = "DELETE FROM participiant WHERE fk_user = ? AND fk_chat = ?;";
	protected final String SQL_DELETE_MESSAGE = "DELETE FROM message WHERE message_id = ?;";
	protected final String SQL_DELETE_MESSAGE_FILE = "DELETE FROM messageFile WHERE fileId = ?;";
	protected final String SQL_DELETE_CHAT = "DELETE FROM chat WHERE chat_id = ?;";
	protected final String SQL_DELETE_UNREAD_MESSAGE = "DELETE FROM unread_messages WHERE fk_user = ? AND fk_message = ?;";

	protected final String SQL_UPDATE_CHAT_LAST_MESSAGE = "UPDATE chat SET fk_last_message = ? WHERE chat_id = ?;";
	protected final String SQL_UPDATE_CHAT_INFO = "UPDATE chat SET description=?,fk_chat_photo=? WHERE chat_id = ?;";
	protected final String SQL_UPDATE_MESSAGE = "UPDATE message SET content = ? WHERE message_id = ?;";

	protected MysqlDataSource source;
	private UserDAO userDAO;
	private Map<String, ChatCreationHandler> handlers;

	public ChatDAOImpl(UserDAO userDAO, MysqlDataSource source) {
		this.userDAO = userDAO;
		this.source = source;
		handlers = new HashMap<>();
		handlers.put("private", new PrivateChatCreationHandler(source, this));
		handlers.put("group", new GroupChatCreationHandler(this));
	}

	@Override
	public void addChat(Chat chat) throws ChatTypeUnsupportedException, ChatDAOException {
		ChatCreationHandler handler = handlers.get(chat.getType());
		if (handler == null) {
			throw new ChatTypeUnsupportedException();
		}
		handler.handle(chat);
	}

	@Override
	public void addMessage(Message message) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);
			Long fileId = null;
			if (message.hasFile()) {
				fileId = addMessageFile(message);
			}
			try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_MESSAGE,
					Statement.RETURN_GENERATED_KEYS)) {
				setParams(statement, message.getChatId(), message.getSender().getId(), message.getContent(),
						Timestamp.from(message.getCreatedAt()), fileId);
				statement.execute();
				message.setMessageId(getGeneratedKey(statement.getGeneratedKeys()));
				try (PreparedStatement statement2 = connection.prepareStatement(SQL_ADD_CHAT_LAST_MESSAGE,
						Statement.NO_GENERATED_KEYS)) {
					setParams(statement2, message.getMessageId(), message.getChatId());
					statement2.execute();
				}
				message.setSender(userDAO.get(message.getSender().getId()));
				markAsUnreadForAllParticipants(message);
				connection.commit();
			} catch (UserNotFoundException | UserDaoException e) {
				e.printStackTrace();
				throw new ChatDAOException(e.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}

	}

	private void markAsUnreadForAllParticipants(Message message) throws ChatDAOException, SQLException {
		Connection connection = source.getConnection();
		Collection<User> participants = getParticipiants(message.getChatId());
		participants.remove(message.getSender());
		try (PreparedStatement statement = connection.prepareStatement(SQL_ADD_UNREAD_MESSAGE)) {
			for (User participant : participants) {
				setParams(statement, participant.getId(), message.getMessageId());
				statement.execute();
			}
		}
	}

	private Long addMessageFile(Message message) throws SQLException {
		try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_MESSAGE_FILE,
				Statement.RETURN_GENERATED_KEYS)) {
			setParams(statement, message.getFile().getName(), message.getFile().getData());
			statement.execute();
			Long fkFile = getGeneratedKey(statement.getGeneratedKeys());
			return fkFile;
		}

	}

//	@Override

	@Override
	public Collection<Chat> getChats(Long participiantId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHATS_ID_OF_PARTICIPIANT,
				Statement.NO_GENERATED_KEYS)) {
			Collection<Chat> chats = new ArrayList<>();
			setParams(statement, participiantId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				try {
					chats.add(getChat(result.getLong("fk_chat")));
				} catch (ResourceNotFoundException ex) {
					System.out.println(ex.getMessage());
				}
			}
			return chats;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public Collection<Message> getChatMessages(Long chatId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHAT_MESSAGES, Statement.NO_GENERATED_KEYS)) {
			Collection<Message> messages = new ArrayList<>();
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				messages.add(getMessage(result.getLong("message_id")));
			}
			return messages;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	protected Long getGeneratedKey(ChatDAOImpl this, ResultSet resultSet) throws SQLException {
		resultSet.next();
		return resultSet.getLong(1);
	}

	protected PreparedStatement getPreparedStatement(String sql, int generatedKey) throws SQLException {
		return source.getConnection().prepareStatement(sql, generatedKey);
	}

	@Override
	public Chat getChat(Long chatId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHAT_BY_ID, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String type = result.getString("chat_type");
				String description = result.getString("description");
				User creator = userDAO.get(result.getLong("fk_owner"));
				Long fkPhotoId = result.getLong("fk_chat_photo");
				ChatPhoto photo = null;
				if (fkPhotoId != 0) {
					photo = getChatPhoto(fkPhotoId);
				}
				Instant createdAt = result.getTimestamp("created_at").toInstant();
				Message lastMessage = null;
				if (result.getLong("fk_last_message") != 0) {
					lastMessage = getMessage(result.getLong("fk_last_message"));
				}
				Chat chat = new Chat();
				chat.setChatId(chatId);
				chat.setType(type);
				chat.setDescription(description);
				chat.setCreator(creator);
				chat.setPhoto(photo);
				chat.setCreatedAt(createdAt);
				chat.setLastMessage(lastMessage);
				chat.setParticipiants(getParticipiants(chatId));
				return chat;
			}
			throw new ResourceNotFoundException("chat with [" + chatId + "] not found");
		} catch (SQLException | UserNotFoundException | UserDaoException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public Collection<User> getParticipiants(Long chatId) throws ChatDAOException {
		Collection<User> users = new ArrayList<>();
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_PARTICIPIANTS, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				users.add(userDAO.get(result.getLong("fk_user")));
			}
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UserDaoException e) {
			e.printStackTrace();
		}
		throw new ChatDAOException("unknown reason");
	}

	@Override
	public Message getMessage(Long messageId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_MESSAGE_BY_ID, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, messageId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				User sender = userDAO.get(result.getLong("fk_sender"));
				String content = result.getString("content");
				Instant time = result.getTimestamp("created_at").toInstant();
				Long chatId = result.getLong("fk_chat");
				Long fkFile = result.getLong("fkFile");
				Message msg = new Message();
				if (fkFile != 0) {
					msg.setFile(getMessageFile(fkFile));
				}
				msg.setMessageId(messageId);
				msg.setContent(content);
				msg.setCreatedAt(time);
				msg.setChatId(chatId);
				msg.setSender(sender);
				return msg;
			}
			throw new ChatDAOException("message by id " + messageId + " not found");
		} catch (SQLException | IllegalArgumentException | UserDaoException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private MessageFile getMessageFile(Long fileId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_MESSAGE_FILE, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, fileId);
			ResultSet result = statement.executeQuery();
			result.next();
			String fileName = result.getString("fileName");
			Blob fileData = result.getBlob("fileData");
			return new MessageFile(fileName, fileData);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private ChatPhoto getChatPhoto(Long fileId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_MESSAGE_FILE, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, fileId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String fileName = result.getString("fileName");
				Blob fileData = result.getBlob("fileData");
				return new ChatPhoto(fileName, fileData);
			}
			throw new ChatDAOException("empty");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	protected void setParams(PreparedStatement statement, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			statement.setObject(i + 1, values[i]);
		}
	}

	@Override
	public void deleteChat(Long chatId) throws ChatDAOException {
		try {
			Chat chat = getChat(chatId);
			Collection<User> users = getParticipiants(chat.getChatId());
			for (User user : users) {
				deleteParticipant(user.getId(), chatId);
			}
			Collection<Message> messages = getChatMessages(chat.getChatId());
			for (Message msg : messages) {
				deleteMessage(msg);
			}
			try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_CHAT, Statement.NO_GENERATED_KEYS)) {
				setParams(statement, chatId);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private void deleteMessage(Message message) throws SQLException {
		Connection connection = source.getConnection();
		connection.setAutoCommit(false);
		try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_MESSAGE_FILE)) {
			setParams(statement, message.getMessageId());
			statement.execute();
		}
		try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_MESSAGE)) {
			setParams(statement, message.getMessageId());
			statement.execute();
		}
		try (PreparedStatement statement = connection.prepareStatement(SQL_GET_LAST_MESSAGE_ACCESSOR)) {
			setParams(statement, message.getMessageId(), message.getChatId());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				Long messageID = result.getLong("message_id");
				try (PreparedStatement statement2 = connection.prepareStatement(SQL_UPDATE_CHAT_LAST_MESSAGE)) {
					setParams(statement2, messageID, message.getChatId());
					statement2.execute();
				}
			}
		}
		connection.commit();

	}

	private void deleteParticipant(Long userId, Long fromChatId) throws SQLException {
		try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_PARTICIPANT, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, userId, fromChatId);
			statement.execute();
		}
	}

	@Override
	public void addParticipant(Chat chat, User user) throws ChatDAOException {
//		String chatType = chat.getType();
//		if (!chatType.equals("group") || !chatType.equals("channel")) {
//			// throw
//			throw new ChatDAOException("It is not allowed to add new participants into a private chat.");
//		}
		try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_PARTICIPIANT, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, user.getId(), chat.getChatId(), Timestamp.from(Instant.now()));
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void removeParticipant(Chat chat, User user) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_PARTICIPANT, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, user.getId(), chat.getChatId());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void updateChat(Chat chat) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_CHAT_INFO,
					Statement.NO_GENERATED_KEYS)) {
				Long fkChatPhoto = null;
				if (chat.getPhoto() != null) {
					fkChatPhoto = insertPhoto(chat.getPhoto(),
							connection.prepareStatement(SQL_INSERT_MESSAGE_FILE, Statement.RETURN_GENERATED_KEYS));
				}
				setParams(statement, chat.getDescription(), fkChatPhoto, chat.getChatId());
				statement.execute();
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private Long insertPhoto(ChatPhoto photo, PreparedStatement statement) throws SQLException {
		if (photo != null) {
			setParams(statement, photo.getName(), photo.getData());
			statement.execute();
			Long fk_chat_photo = getGeneratedKey(statement.getGeneratedKeys());
			return fk_chat_photo;
		}
		return null;
	}

	@Override
	public void removeMessage(Long messageId) throws ChatDAOException {
		try {
			Message msg = getMessage(messageId);
			deleteMessage(msg);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void updateMessage(Message message) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_MESSAGE)) {
				setParams(statement, message.getContent(), message.getMessageId());
				statement.execute();
			}
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public Collection<Message> getUnreadMessages(User participant, Chat chat) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_UNREAD_MESSAGES, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chat.getChatId(), participant.getId());
			ResultSet result = statement.executeQuery();
			Collection<Message> unreadMessages = new ArrayList<>();
			while (result.next()) {
				unreadMessages.add(getMessage(result.getLong("fk_message")));
			}
			return unreadMessages;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	@Override
	public void updateReadStatus(User user, Message message) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE_UNREAD_MESSAGE)) {
				setParams(statement, user.getId(), message.getMessageId());
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

}
