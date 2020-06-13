package webservices.telegram.exception;

public class UserDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserDaoException(String message) {
		super(message);
	}

	public UserDaoException(Throwable cause) {
		super(cause);
	}

}
