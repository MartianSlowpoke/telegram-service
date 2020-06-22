package webservices.telegram.controller;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttribute;

import webservices.telegram.dto.chat.ChatListResponse;
import webservices.telegram.dto.chat.ParticipantRequest;
import webservices.telegram.exception.ResourceNotFoundException;
import webservices.telegram.exception.chat.ChatDAOException;
import webservices.telegram.exception.chat.ChatTypeUnsupportedException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.ChatPhoto;
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
		chat.setCreator(user);
		service.add(chat);
		return chat;
	}

	@RequestMapping(method = RequestMethod.GET, value = {
			"{id}" }, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Chat getChatById(@PathVariable("id") Long chatId) throws ChatDAOException {
		return service.getChat(chatId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "{id}/photo", produces = { MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	@ResponseBody
	public ChatPhoto getChatPhoto(@PathVariable("id") Long chatId) throws ChatDAOException {
		Chat chat = service.getChat(chatId);
		if (chat.hasPhoto()) {
			return chat.getPhoto();
		}
		throw new ResourceNotFoundException("chat [" + chatId + "] does not have any photo");
	}

	@RequestMapping(method = RequestMethod.POST, value = {
			"{chat-id}/participants" }, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public void addParticipant(@PathVariable("chat-id") Long chat_id, @RequestBody ParticipantRequest req)
			throws ChatDAOException {
		service.addParticipant(chat_id, req.getUserId());
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{chat-id}/participants/{user-id}", consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public void removeParticipant(@PathVariable("chat-id") Long chatId, @PathVariable("user-id") Long userId)
			throws ChatDAOException {
		service.removeParticipant(chatId, userId);
	}

	@RequestMapping(method = RequestMethod.GET, value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ChatListResponse getChats(@NonNull @SessionAttribute("user") User user) throws ChatDAOException {
		Collection<Chat> chats = service.getChats(user.getId());
		return new ChatListResponse(chats);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public void deleteChat(@PathVariable("id") Long chatId) throws ChatDAOException {
		service.deleteChat(chatId);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "{chat-id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public Chat updateChatInfo(@PathVariable("chat-id") Long chatId, @RequestBody Chat chat) throws ChatDAOException {
		chat.setChatId(chatId);
		Chat dbChat = service.getChat(chatId);
		dbChat.setDescription(chat.getDescription());
		dbChat.setPhoto(chat.getPhoto());
		service.update(dbChat);
		return dbChat;
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
