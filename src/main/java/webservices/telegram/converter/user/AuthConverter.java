package webservices.telegram.converter.user;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.model.user.Authentication;

public class AuthConverter extends AbstractHttpMessageConverter<Authentication> {

	public AuthConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Authentication.class;
	}

	@Override
	protected Authentication readInternal(Class<? extends Authentication> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void writeInternal(Authentication t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		JSONObject json = new JSONObject();
		json.put("email", t.getEmail());
		json.put("password", t.getPassword());
		outputMessage.getBody().write(json.toJSONString().getBytes());
	}

}
