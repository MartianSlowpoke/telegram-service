package webservices.telegram.converter.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MultipartFormParser;
import webservices.telegram.exception.BadRequestDataException;
import webservices.telegram.model.chat.Chat;

public class ChatMultipartConverter extends AbstractHttpMessageConverter<Chat> {

	public ChatMultipartConverter() {
		super(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Chat.class;
	}

	@Override
	protected Chat readInternal(Class<? extends Chat> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			MultipartFormParser form = new MultipartFormParser();
			form.parse(inputMessage);
			Chat chat = new Chat(form.get("type"), parse(form.get("participants")));
			return chat;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestDataException(e.getMessage());
		}

	}

	@Override
	protected void writeInternal(Chat t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
	}

	private Collection<String> parse(String str) {
		Collection<String> tokens = new ArrayList<>();
		for (String token : str.split(";")) {
			tokens.add(token);
		}
		return tokens;
	}

}
