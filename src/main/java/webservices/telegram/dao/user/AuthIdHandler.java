package webservices.telegram.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class AuthIdHandler implements ResultSetHandler<Long> {

	@Override
	public Long handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			Long authId = rs.getLong(1);
			return authId;
		}
		return null;
	}

}
