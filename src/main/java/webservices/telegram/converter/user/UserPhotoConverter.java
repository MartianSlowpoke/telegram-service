package webservices.telegram.converter.user;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.MimeType;

import webservices.telegram.model.user.UserPhoto;

public class UserPhotoConverter extends AbstractHttpMessageConverter<UserPhoto> {

	public UserPhotoConverter() {
		super(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == UserPhoto.class;
	}

	@Override
	protected UserPhoto readInternal(Class<? extends UserPhoto> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(UserPhoto user, HttpOutputMessage response)
			throws IOException, HttpMessageNotWritableException {
		try {
			InputStream in = user.getFileData().getBinaryStream();
			int fileLength = in.available();
			String mimeType = URLConnection.guessContentTypeFromName(user.getFileName());
			MimeType media = MimeType.valueOf(mimeType);
			response.getHeaders().setContentType(new MediaType(media.getType(), media.getSubtype()));
			response.getHeaders().setContentLength(fileLength);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("filename=\"%s\"", user.getFileName());
			response.getHeaders().add(headerKey, headerValue);
			IOUtils.copy(in, response.getBody());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

}
