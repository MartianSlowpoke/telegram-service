package webservices.telegram.converter.user;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MultipartFormParser;
import webservices.telegram.exception.user.NotValidAuthDataException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.AuthenticationBuilder;

public class AuthMultipartConverter extends AbstractHttpMessageConverter<Authentication> {

	public AuthMultipartConverter() {
		super(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Authentication.class;
	}

	@Override
	protected Authentication readInternal(Class<? extends Authentication> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			MultipartFormParser form = new MultipartFormParser();
			form.parse(inputMessage);
			AuthenticationBuilder auth = new AuthenticationBuilder();
			return auth.email(form.get("email")).password(form.get("password")).build();
		} catch (FileUploadException e) {
			e.printStackTrace();
			throw new NotValidAuthDataException("bad photo file");
		}

	}

	@Override
	protected void writeInternal(Authentication t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

	}

}
