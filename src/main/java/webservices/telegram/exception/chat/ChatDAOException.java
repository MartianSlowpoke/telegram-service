package webservices.telegram.exception.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "exception occured at a storage dao")
public class ChatDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public ChatDAOException() {
	}

	public ChatDAOException(String message) {
		super(message);
	}

}
