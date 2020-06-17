package webservices.telegram.exception.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid chat registration data")
public class ChatTypeUnsupportedException extends Exception {

	public ChatTypeUnsupportedException() {
	}

	public ChatTypeUnsupportedException(String message) {
		super(message);
	}

}
