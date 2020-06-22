package webservices.telegram.converter.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MultipartFormParser;
import webservices.telegram.exception.BadRequestDataException;
import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.ChatPhoto;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserBuilder;

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
			Chat chat = new Chat();
			chat.setParticipiants(obtainUsers(form.get("participants")));
			chat.setType(form.get("type"));
			chat.setName(form.get("name"));
			chat.setDescription(form.get("description"));
			if (form.getFileItem("photo") != null) {
				if (form.getFileItem("photo").getSize() != 0 && form.getFileName("photo") != null) {
					chat.setPhoto(
							new ChatPhoto(form.getFileName("photo"), new SerialBlob(form.getFileItem("photo").get())));
				}
			}
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

	private Collection<User> obtainUsers(String str) {
		if (str != null) {
			Collection<User> users = new ArrayList<>();
			for (String token : str.split(";")) {
				users.add(new UserBuilder().id(Long.parseLong(token)).build());
			}
			return users;
		}
		return null;
	}

}
