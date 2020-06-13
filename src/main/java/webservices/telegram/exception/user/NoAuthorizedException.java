package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class NoAuthorizedException extends RuntimeException {

	public NoAuthorizedException(String message) {
		super(message);
	}

}
