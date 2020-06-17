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
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.chat.MessageFile;
import webservices.telegram.model.user.User;

public class ChatDAOImpl implements ChatDAO {

	protected final static String SQL_INSERT_PRIVATE_CHAT = "INSERT INTO chat (chat_type,created_at) VALUES (?,?);";
	protected final static String SQL_INSERT_PARTICIPIANT = "INSERT INTO participiant (fk_user, fk_chat, created_at) VALUES (?,?,?);";
	protected final String SQL_INSERT_MESSAGE = "INSERT INTO message (fk_chat, fk_sender,content,created_at, fkFile) VALUES (?,?,?,?,?);";
	protected final String SQL_INSERT_MESSAGE_FILE = "INSERT INTO messageFile (fileName,fileData) VALUES (?,?);";;

	protected final String SQL_GET_PARTICIPIANTS = "SELECT fk_user,fk_chat FROM participiant WHERE fk_chat = ?;";
	protected final String SQL_GET_CHATS_ID_OF_PARTICIPIANT = "SELECT fk_chat FROM participiant WHERE fk_user = ?;";
	protected final String SQL_GET_CHAT_BY_ID = "SELECT chat_id, chat_type,fk_last_message,created_at FROM chat WHERE chat_id = ?;";
	protected final String SQL_GET_CHAT_MESSAGES = "SELECT message_id FROM message WHERE fk_chat = ?;";
	protected final String SQL_GET_MESSAGE_BY_ID = "SELECT message_id,fk_chat,fk_sender,content,fkFile,created_at FROM message WHERE message_id = ?";
	protected final String SQL_GET_MESSAGE_FILE = "SELECT fileName,fileData FROM messageFile WHERE fileId = ?;";

	protected final String SQL_DELETE_PARTICIPANT = "DELETE FROM participiant WHERE fk_user = ? AND fk_chat = ?;";
	protected final String SQL_DELETE_MESSAGE = "DELETE FROM message WHERE message_id = ?;";
	protected final String SQL_DELETE_MESSAGE_FILE = "DELETE FROM messageFile WHERE fileId = ?;";
	protected final String SQL_DELETE_CHAT = "DELETE FROM chat WHERE chat_id = ?;";

	private MysqlDataSource source;
	private UserDAO userDAO;
	private Map<String, ChatCreationHandler> handlers;

	public ChatDAOImpl(UserDAO userDAO, MysqlDataSource source) {
		this.userDAO = userDAO;
		this.source = source;
		handlers = new HashMap<>();
		handlers.put("private", new PrivateChatCreationHandler(source, this));
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
			try (PreparedStatement statement = getPreparedStatement(SQL_INSERT_MESSAGE,
					Statement.RETURN_GENERATED_KEYS)) {
				setParams(statement, message.getChatId(), message.getSender().getId(), message.getContent(),
						Timestamp.from(message.getCreatedAt()), fileId);
				statement.execute();
				message.setMessageId(getGeneratedKey(statement.getGeneratedKeys()));
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
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

	protected PreparedStatement getPreparedStatement(ChatDAOImpl this, String sql, int generatedKey)
			throws SQLException {
		return this.source.getConnection().prepareStatement(sql, generatedKey);
	}

	@Override
	public Chat getChat(Long chatId) throws ChatDAOException {
		try (PreparedStatement statement = getPreparedStatement(SQL_GET_CHAT_BY_ID, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, chatId);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String type = result.getString("chat_type");
				Instant createdAt = result.getTimestamp("created_at").toInstant();
				if (result.getLong("fk_last_message") != 0) {
					Message last = getMessage(result.getLong("fk_last_message"));
					Chat chat = new Chat(chatId, type, last, createdAt, getParticipiants(chatId));
					return chat;
				}
				return new Chat(chatId, type, null, createdAt, getParticipiants(chatId));
			}
			throw new ResourceNotFoundException("chat with [" + chatId + "] not found");
		} catch (SQLException e) {
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
				Long fkFile = result.getLong("fkFile");
				if (fkFile == 0) {
					return new Message(messageId, sender, content, time);
				}
				return new Message(messageId, sender, content, getMessageFile(fkFile), time);
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
				deleteMessage(msg.getMessageId());
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

	private void deleteMessage(Long messageId) throws SQLException {
		try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_MESSAGE_FILE, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, messageId);
			statement.execute();
		}
		try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_MESSAGE, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, messageId);
			statement.execute();
		}
	}

	private void deleteParticipant(Long userId, Long fromChatId) throws SQLException {
		try (PreparedStatement statement = getPreparedStatement(SQL_DELETE_PARTICIPANT, Statement.NO_GENERATED_KEYS)) {
			setParams(statement, userId, fromChatId);
			statement.execute();
		}
	}

}
