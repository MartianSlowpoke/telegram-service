package webservices.telegram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttribute;

import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.user.User;
import webservices.telegram.service.chat.ChatService;

@Controller(value = "chat-controller")
@RequestMapping(value = "chats")
public class ChatController {

	private ChatService service;

	public ChatController(ChatService service) {
		super();
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Chat create(@SessionAttribute("user") User user, @RequestBody Chat chat)
			throws ChatDAOException, ChatTypeUnsupportedException {
		service.add(chat);
		return chat;
	}

	@ExceptionHandler
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Exception> handle(Exception e) {
		if (e.getClass().isAnnotationPresent(ResponseStatus.class)) {
			HttpStatus statusCode = e.getClass().getAnnotation(ResponseStatus.class).code();
			ResponseEntity<Exception> entity = new ResponseEntity<Exception>(e, statusCode);
			return entity;
		}
		return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
