package webservices.telegram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "bad input data")
public class BadRequestDataException extends RuntimeException {

	public BadRequestDataException(String message) {
		super(message);
	}

}
