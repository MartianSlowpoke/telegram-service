package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UserDAOException extends RuntimeException{
	
	public UserDAOException(String message) {
		super(message);
	}
	
}
