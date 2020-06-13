package webservices.telegram.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "photo is not found")
public class UserPhotoNotFoundException extends IllegalArgumentException {

	public UserPhotoNotFoundException(String message) {
		super(message);
	}

}
