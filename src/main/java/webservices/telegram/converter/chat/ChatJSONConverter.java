package webservices.telegram.converter.chat;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.ChatJsonMapper;
import webservices.telegram.model.chat.Chat;

public class ChatJSONConverter extends AbstractHttpMessageConverter<Chat> {

	public ChatJSONConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Chat.class;
	}

	@Override
	protected Chat readInternal(Class<? extends Chat> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(Chat chat, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		JSONObject json = ChatJsonMapper.chatToJson(chat);
		outputMessage.getBody().write(json.toJSONString().getBytes());
	}

}
