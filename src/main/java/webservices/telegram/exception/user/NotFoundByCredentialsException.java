package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "a user is not found by the given credentials")
@SuppressWarnings("serial")
public class NotFoundByCredentialsException extends IllegalArgumentException {
	
	

}
