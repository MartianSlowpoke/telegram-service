package webservices.telegram.converter.chat;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.ChatJsonMapper;
import webservices.telegram.dto.chat.ChatListResponse;

public class ChatListJSONConverter extends AbstractHttpMessageConverter<ChatListResponse> {

	public ChatListJSONConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == ChatListResponse.class;
	}

	@Override
	protected ChatListResponse readInternal(Class<? extends ChatListResponse> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(ChatListResponse chats, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		String json = ChatJsonMapper.chatsToJson(chats.getChats());
		outputMessage.getBody().write(json.getBytes());
	}

}
