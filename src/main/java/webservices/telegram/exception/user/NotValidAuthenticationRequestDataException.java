package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "authentication data are in wrong format")
public class NotValidAuthenticationRequestDataException extends IllegalArgumentException {

	public NotValidAuthenticationRequestDataException() {
	}

}
