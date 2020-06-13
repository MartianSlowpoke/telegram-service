package webservices.telegram.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.dbutils.ResultSetHandler;

import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserBuilder;

public class UserHandler implements ResultSetHandler<Collection<User>> {

	@Override
	public Collection<User> handle(ResultSet rs) throws SQLException {
		Collection<User> users = new ArrayList<>();
		while (rs.next()) {
			UserBuilder builder = new UserBuilder();
			User user = builder.id(rs.getLong("user_id")).login(rs.getString("login"))
					.firstName(rs.getString("firstName")).lastName(rs.getString("lastName"))
					.description(rs.getString("description")).lastSeen(rs.getString("lastSeen"))
					.isDeleted(rs.getBoolean("isDeleted")).isOnline(rs.getBoolean("isOnline"))
					.createdAt(rs.getTimestamp("createdAt").toInstant()).build();
			users.add(user);
		}
		return users;
	}

}
