package webservices.telegram.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.mysql.cj.jdbc.MysqlDataSource;

import webservices.telegram.exception.DaoIllegalException;
import webservices.telegram.exception.UserDaoException;
import webservices.telegram.exception.user.NotFoundByCredentialsException;
import webservices.telegram.exception.user.NotValidAuthDataException;
import webservices.telegram.exception.user.UserNotFoundException;
import webservices.telegram.exception.user.UserPhotoNotFoundException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.AuthenticationBuilder;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserPhoto;

public class DatabaseUserDaoImpl implements UserDAO {

	private final String SQL_INSERT_USER = "INSERT INTO user (login,firstName,lastName,description,lastSeen,isDeleted,isOnline,createdAt) VALUES (?,?,?,?,?,?,?,?);";
	private final String SQL_INSERT_AUTH = "INSERT INTO authentication (userId,email,password) VALUES (?,?,?);";
	private final String SQL_INSERT_PHOTO = "INSERT INTO UserPhoto (fileName,fileData,userId) VALUES (?,?,?)";

	private final String SQL_GET_USER_PHOTO = "SELECT id,fileName,fileData,userId FROM UserPhoto WHERE userId = ?";
	private final String SQL_GET_USER_ID_USING_AUTH = "SELECT userId FROM authentication WHERE email = ? AND password = ?;";
	private final String SQL_GET_USER_BY_ID = "SELECT user_id,login,firstName,lastName,description,lastSeen,isDeleted,isOnline,createdAt FROM user WHERE user_id = ?;";
	private final String SQL_GET_ALL_USERS = "SELECT user_id,login,firstName,lastName,description,lastSeen,isDeleted,isOnline,createdAt FROM user;";
	private final String SQL_GET_USER_AUTH = "SELECT email,password FROM authentication WHERE userId = ?;";

	private final String SQL_UPDATE_AUTH = "UPDATE authentication SET email = ?, password = ? WHERE userId = ?;";
	private final String SQL_UPDATE_USER = "UPDATE user SET login = ? , firstName = ? , lastName = ? , description = ? WHERE user_id = ?;";
	private final String SQL_UPDATE_USER_PHOTO = "UPDATE UserPhoto SET fileName = ? , fileData = ? WHERE userId = ?;";
	private final String SQL_UPDATE_ONLINE_STATUS = "UPDATE user SET isOnline = ? , lastSeen = ? WHERE user_id = ?;";

	private final String SQL_DELETE_USER = "UPDATE user SET firstName = NULL, lastName = NULL, description = NULL, photo = NULL, lastSeen = 'last seen a long time ago', isDeleted = 1, isOnline = 0 WHERE user_id = ?;";
	private final String SQL_DELETE_AUTH = "DELETE FROM authentication WHERE userId = ?;";
	private final String SQL_DELETE_USER_PHOTOS = "DELETE FROM UserPhoto WHERE userId = ?;";

	private final String SQL_EXIST_EMAIL = "SELECT userId FROM authentication WHERE email = ?;";
	private final String SQL_EXIST_LOGIN = "SELECT user_id FROM user WHERE login = ?;";

	private MysqlDataSource source;
	private QueryRunner runner;

	public DatabaseUserDaoImpl() {
		this.initSource();
		this.runner = new QueryRunner(source);
	}

	private void initSource(DatabaseUserDaoImpl this) {
		this.source = new MysqlDataSource();
		this.source.setUser("root");
		this.source.setPassword("1111");
		this.source.setDatabaseName("chat");
		this.source.setURL(source.getURL()
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
	}

	@Override
	public void add(User user, Authentication authentication) throws UserDaoException, IllegalArgumentException {
		if (user.getId() != null)
			throw new IllegalArgumentException("user id must be null");
		if (user.getLogin() == null)
			throw new IllegalArgumentException("user login is null");
		if (authentication.getEmail() == null || authentication.getPassword() == null)
			throw new IllegalArgumentException("not valid credentials");
		boolean existEmail = this.existEmail(authentication.getEmail());
		boolean existLogin = this.existLogin(user.getLogin());
		if (existEmail || existLogin)
			throw new NotValidAuthDataException("existEmail = " + existEmail + "; existLogin = " + existLogin);
		try {
			Connection connection = source.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement statement = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			setParams(statement, user.getLogin(), user.getFirstName(), user.getLastName(), user.getDescription(),
					user.getLastSeen(), user.getIsDeleted(), user.getIsOnline(), Timestamp.from(user.getCreatedAt()));
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Long userId = resultSet.getLong(1);
			user.setId(userId);
			statement.close();
			statement = connection.prepareStatement(SQL_INSERT_AUTH);
			setParams(statement, userId, authentication.getEmail(), authentication.getPassword());
			statement.executeUpdate();
			statement.close();
			statement = connection.prepareStatement(SQL_INSERT_PHOTO);
			setParams(statement, null, null, user.getId());
			statement.executeUpdate();
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public User logIn(Authentication authentication) throws UserDaoException, IllegalArgumentException {
		try {
			ResultSetHandler<Long> authHandler = new AuthIdHandler();
			Connection connection = source.getConnection();
			Long userId = runner.query(connection, SQL_GET_USER_ID_USING_AUTH, authHandler, authentication.getEmail(),
					authentication.getPassword());
			if (userId == null)
				// return 404 http error code
				throw new NotFoundByCredentialsException();
			return get(userId);
		} catch (SQLException e) {
			throw new UserDaoException(e);
		}
	}

	@Override
	public User get(Long id) throws UserDaoException, UserNotFoundException {
		try {
			Connection connection = source.getConnection();
			ResultSetHandler<Collection<User>> userHandler = new UserHandler();
			Collection<User> users = runner.query(connection, SQL_GET_USER_BY_ID, userHandler, id);
			if (users.size() == 0)
				throw new UserNotFoundException("a user with id " + id + " is not found");
			User user = users.iterator().next();
			try {
				user.setPhoto(getPhoto(user.getId()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public User get(String login) throws UserDaoException, IllegalArgumentException {

		return null;
	}

	@Override
	public UserPhoto getPhoto(Long userId) throws UserDaoException, IllegalArgumentException {
		try {
			Connection connection = source.getConnection();
			ResultSetHandler<UserPhoto> userHandler = new UserPhotoHandler();
			UserPhoto user = runner.query(connection, SQL_GET_USER_PHOTO, userHandler, userId);
			if (user == null) {
				throw new UserPhotoNotFoundException("UserPhoto is not found");
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public void update(Authentication authentication) throws UserDaoException, IllegalArgumentException {
		try {
			Connection connection = source.getConnection();
			runner.update(connection, SQL_UPDATE_AUTH, authentication.getEmail(), authentication.getPassword(),
					authentication.getUserId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(User user) throws UserDaoException, IllegalArgumentException {
		if (user.getId() == null)
			throw new IllegalArgumentException("user id is null");
		if (user.getLogin() == null)
			throw new IllegalArgumentException("user login is null");
		try {
			Connection connection = source.getConnection();
			runner.update(connection, SQL_UPDATE_USER, user.getLogin(), user.getFirstName(), user.getLastName(),
					user.getDescription(), user.getId());
			UserPhoto photo = user.getPhoto();
			if (photo != null) {
				runner.update(connection, SQL_UPDATE_USER_PHOTO, photo.getFileName(), photo.getFileData(),
						user.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public void updateOnlineStatus(User user) throws UserDaoException, IllegalArgumentException {
		if (user.getId() == null)
			throw new IllegalArgumentException("user id is null");
		try {
			Connection connection = source.getConnection();
			runner.update(connection, SQL_UPDATE_ONLINE_STATUS, user.getIsOnline(), user.getLastSeen(), user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public void delete(User user) throws UserDaoException, IllegalArgumentException {
		if (user.getId() == null)
			throw new IllegalArgumentException("user id is null");
		try {
			runner.update(SQL_DELETE_AUTH, user.getId());
			runner.update(SQL_DELETE_USER, user.getId());
			runner.update(SQL_DELETE_USER_PHOTOS, user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public boolean existEmail(String email) throws UserDaoException, IllegalArgumentException {
		if (email == null || email == "")
			throw new IllegalArgumentException("empty email");
		try {
			ResultSetHandler<Long> tempHandler = new ResultSetHandler<>() {
				public Long handle(ResultSet rs) throws SQLException {
					if (rs.next())
						return rs.getLong(1);
					return null;
				}
			};
			Long userId = runner.query(SQL_EXIST_EMAIL, tempHandler, email);
			if (userId == null)
				return false;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public boolean existLogin(String login) throws UserDaoException, IllegalArgumentException {
		if (login == null || login == "")
			throw new IllegalArgumentException("empty login");
		try {
			ResultSetHandler<Long> tempHandler = new ResultSetHandler<>() {
				public Long handle(ResultSet rs) throws SQLException {
					if (rs.next())
						return rs.getLong(1);
					return null;
				}
			};
			Long userId = runner.query(SQL_EXIST_LOGIN, tempHandler, login);
			if (userId == null)
				return false;
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public Collection<User> getAll(String loginMatch) throws UserDaoException, IllegalArgumentException {
		return null;
	}

	@Override
	public Collection<User> getAll() throws UserDaoException, IllegalArgumentException {
		try {
			Connection connection = source.getConnection();
			ResultSetHandler<Collection<User>> userHandler = new UserHandler();
			Collection<User> users = runner.query(connection, SQL_GET_ALL_USERS, userHandler);
			if (users.size() == 0)
				throw new IllegalArgumentException(DaoIllegalException.NO_USERS.toString());
			for (User user : users) {
				try {
					user.setPhoto(getPhoto(user.getId()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	@Override
	public Authentication getAuth(Long userId) throws UserDaoException, IllegalArgumentException {
		try {
			Connection connection = source.getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_AUTH);
			statement.setLong(1, userId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				AuthenticationBuilder authBuilder = new AuthenticationBuilder();
				Authentication auth = authBuilder.email(resultSet.getString("email"))
						.password(resultSet.getString("password")).build();
				statement.close();
				return auth;
			}
			throw new UserNotFoundException("Authentication is not found by userId = " + userId);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UserDaoException(e);
		}
	}

	private void setParams(PreparedStatement statement, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			statement.setObject(i + 1, values[i]);
		}
	}

}
