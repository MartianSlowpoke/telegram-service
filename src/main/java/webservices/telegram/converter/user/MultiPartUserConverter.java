package webservices.telegram.converter.user;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.converter.MultipartFormParser;
import webservices.telegram.exception.user.NotValidAuthDataException;
import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserBuilder;
import webservices.telegram.model.user.UserPhotoBuilder;

public class MultiPartUserConverter extends AbstractHttpMessageConverter<User> {

	public MultiPartUserConverter() {
		super(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == User.class;
	}

	@Override
	protected User readInternal(Class<? extends User> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			MultipartFormParser form = new MultipartFormParser();
			form.parse(inputMessage);
			UserBuilder userBuilder = new UserBuilder();
			User user = userBuilder.login(form.get("login")).firstName(form.get("firstName"))
					.lastName(form.get("lastName")).description(form.get("description")).build();
			UserPhotoBuilder photoBuilder = new UserPhotoBuilder();
			photoBuilder.fileName(form.getFileName("photo"));
			if (form.getFileItem("photo").get() != null) {
				photoBuilder.fileData(new SerialBlob(form.getFileItem("photo").get()));
				user.setPhoto(photoBuilder.build());
			}
			return user;
		} catch (FileUploadException | SQLException e) {
			e.printStackTrace();
			throw new NotValidAuthDataException("bad photo file");
		}
	}

	@Override
	protected void writeInternal(User t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

	}

}
