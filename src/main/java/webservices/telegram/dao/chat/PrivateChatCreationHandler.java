package webservices.telegram.dao.chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.mysql.cj.jdbc.MysqlDataSource;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.user.User;

public class PrivateChatCreationHandler implements ChatCreationHandler {

	private MysqlDataSource source;
	private ChatDAOImpl chatDAO;

	public PrivateChatCreationHandler(MysqlDataSource source, ChatDAOImpl chatDAO) {
		super();
		this.source = source;
		this.chatDAO = chatDAO;
	}

	@Override
	public void handle(Chat chat) throws ChatDAOException {
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);
			try (PreparedStatement statement = chatDAO.getPreparedStatement(ChatDAOImpl.SQL_INSERT_PRIVATE_CHAT,
					Statement.RETURN_GENERATED_KEYS)) {
				chatDAO.setParams(statement, chat.getType(), Timestamp.from(chat.getCreatedAt()));
				statement.executeUpdate();
				chat.setChatId(chatDAO.getGeneratedKey(statement.getGeneratedKeys()));
			}
			try (PreparedStatement statement = chatDAO.getPreparedStatement(ChatDAOImpl.SQL_INSERT_PARTICIPIANT,
					Statement.NO_GENERATED_KEYS)) {
				for (User participiant : chat.getParticipiants()) {
					chatDAO.setParams(statement, new Object[] { participiant.getId(), chat.getChatId(),
							Timestamp.from(chat.getCreatedAt()) });
					statement.execute();
				}
			}
			chat.setParticipiants(chatDAO.getParticipiants(chat.getChatId()));
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

}
