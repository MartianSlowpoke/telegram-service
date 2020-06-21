package webservices.telegram.converter.user;

import java.io.IOException;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.fileupload.FileItem;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MultipartFormParser;
import webservices.telegram.dto.user.UserRegistrationRequest;
import webservices.telegram.exception.BadRequestDataException;
import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.AuthenticationBuilder;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserBuilder;
import webservices.telegram.model.user.UserPhotoBuilder;

public class UserRegistrationMultipartConverter extends AbstractHttpMessageConverter<UserRegistrationRequest> {

	public UserRegistrationMultipartConverter() {
		super(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == UserRegistrationRequest.class;
	}

	@Override
	protected UserRegistrationRequest readInternal(Class<? extends UserRegistrationRequest> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		try {
			MultipartFormParser form = new MultipartFormParser();
			form.parse(inputMessage);
			UserBuilder userBuilder = new UserBuilder();
			User user = userBuilder.login(form.get("login")).firstName(form.get("firstName"))
					.lastName(form.get("lastName")).description(form.get("description")).build();
			UserPhotoBuilder photoBuilder = new UserPhotoBuilder();
			photoBuilder.fileName(form.getFileName("photo"));
			FileItem photo = form.getFileItem("photo");
			if (photo != null) {
				if (form.getFileItem("photo").getSize() != 0 && form.getFileName("photo") != null) {
					photoBuilder.fileData(new SerialBlob(form.getFileItem("photo").get()));
					user.setPhoto(photoBuilder.build());
				}
			}
			AuthenticationBuilder authBuild = new AuthenticationBuilder();
			Authentication auth = authBuild.email(form.get("email")).password(form.get("password")).build();
			return new UserRegistrationRequest(user, auth);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestDataException(e.getMessage());
		}
	}

	@Override
	protected void writeInternal(UserRegistrationRequest t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

	}

}
