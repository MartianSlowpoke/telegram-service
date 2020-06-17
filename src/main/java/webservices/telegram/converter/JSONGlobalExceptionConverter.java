package webservices.telegram.converter;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ResponseStatus;

import webservices.telegram.exception.BadRequestDataException;
import webservices.telegram.exception.ResourceNotFoundException;
import webservices.telegram.exception.user.NotValidAuthDataException;

public class JSONGlobalExceptionConverter extends AbstractHttpMessageConverter<Exception> {

	private Class<?>[] classes = new Class<?>[] { NotValidAuthDataException.class, BadRequestDataException.class,
			ResourceNotFoundException.class };

	public JSONGlobalExceptionConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		for (Class<?> cls : classes) {
			if (cls == clazz)
				return true;
		}
		return false;
	}

	@Override
	protected Exception readInternal(Class<? extends Exception> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void writeInternal(Exception t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		if (t.getClass().isAnnotationPresent(ResponseStatus.class)) {
			ResponseStatus statusCode = t.getClass().getAnnotation(ResponseStatus.class);
			JSONObject json = new JSONObject();
			json.put("status", statusCode.code().value());
			json.put("reason", statusCode.reason());
			json.put("message", t.getMessage());
			outputMessage.getBody().write(json.toJSONString().getBytes());
		}
	}

}
