package webservices.telegram.dao.chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.ChatPhoto;
import webservices.telegram.model.user.User;

public class GroupChatCreationHandler implements ChatCreationHandler {

	private ChatDAOImpl chatDAO;

	public GroupChatCreationHandler(ChatDAOImpl chatDAO) {
		this.chatDAO = chatDAO;
	}

	@Override
	public void handle(Chat chat) throws ChatDAOException {
		String chatType = chat.getType();
		if (!chatType.equals("group")) {
			throw new ChatDAOException("unrecognized chat tye");
		}
		try {
			try (Connection connection = chatDAO.source.getConnection()) {
				connection.setAutoCommit(false);
				Long fkChatPhoto = null;
				if (chat.getPhoto() != null) {
					fkChatPhoto = insertPhoto(chat.getPhoto(), connection
							.prepareStatement(chatDAO.SQL_INSERT_MESSAGE_FILE, Statement.RETURN_GENERATED_KEYS));
				}
				try (PreparedStatement statement = connection.prepareStatement(chatDAO.SQL_INSERT_GROUP_CHAT,
						Statement.RETURN_GENERATED_KEYS)) {
					chatDAO.setParams(statement, chat.getType(), chat.getDescription(), chat.getCreator().getId(),
							fkChatPhoto);
					statement.execute();
					Long chatId = chatDAO.getGeneratedKey(statement.getGeneratedKeys());
					chat.setChatId(chatId);
				}
				chatDAO.addParticipant(chat, chat.getCreator());
				for (User participant : chat.getParticipiants()) {
					chatDAO.addParticipant(chat, participant);
				}
				chat.setParticipiants(chatDAO.getParticipiants(chat.getChatId()));
				connection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ChatDAOException(e.getMessage());
		}
	}

	private Long insertPhoto(ChatPhoto photo, PreparedStatement statement) throws SQLException {
		if (photo != null) {
			chatDAO.setParams(statement, photo.getName(), photo.getData());
			statement.execute();
			Long fk_chat_photo = chatDAO.getGeneratedKey(statement.getGeneratedKeys());
			return fk_chat_photo;
		}
		return null;
	}

}
