package webservices.telegram.exception.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "input chat format is wrong")
public class InputChatNotValidException extends IllegalArgumentException {

}
