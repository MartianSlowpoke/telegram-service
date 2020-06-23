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
			builder.id(rs.getLong("user_id")).login(rs.getString("login")).firstName(rs.getString("firstName"))
					.lastName(rs.getString("lastName")).description(rs.getString("description"))
					.isDeleted(rs.getBoolean("isDeleted")).isOnline(rs.getBoolean("isOnline"))
					.createdAt(rs.getTimestamp("createdAt").toInstant());
			if (rs.getTimestamp("lastSeen") != null) {
				builder.lastSeen(rs.getTimestamp("lastSeen").toInstant());
			}
			users.add(builder.build());
		}
		return users;
	}

}
