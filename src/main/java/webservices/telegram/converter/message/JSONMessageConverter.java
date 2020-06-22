package webservices.telegram.converter.message;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MessageJsonMapper;
import webservices.telegram.model.chat.Message;

public class JSONMessageConverter extends AbstractHttpMessageConverter<Message> {

	public JSONMessageConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Message.class;
	}

	@Override
	protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(Message t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		JSONObject json = MessageJsonMapper.messageToJson(t);
		outputMessage.getBody().write(json.toJSONString().getBytes());
	}

}
