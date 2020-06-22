package webservices.telegram.converter.message;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MessageJsonMapper;
import webservices.telegram.dto.chat.MessageListResponse;

public class JSONMessageListConverter extends AbstractHttpMessageConverter<MessageListResponse> {

	public JSONMessageListConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == MessageListResponse.class;
	}

	@Override
	protected MessageListResponse readInternal(Class<? extends MessageListResponse> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(MessageListResponse t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(MessageJsonMapper.messagesToJson(t.getMessages()).getBytes());
	}

}
