package webservices.telegram.dao.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class GeneratedKeyHandler implements ResultSetHandler<Long> {

	@Override
	public Long handle(ResultSet rs) throws SQLException {
		ResultSet result = rs.getStatement().getGeneratedKeys();
		if (result.next()) {
			Long id = result.getLong(1);
			return id;
		} else {
			throw new SQLException("The creation of the user id is failured");
		}
	}

}
