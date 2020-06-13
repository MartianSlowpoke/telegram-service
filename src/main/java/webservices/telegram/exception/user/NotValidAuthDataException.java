package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NotValidAuthDataException extends IllegalArgumentException {

	public NotValidAuthDataException(String message) {
		super(message);
	}

}
