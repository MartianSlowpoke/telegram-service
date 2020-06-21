package webservices.telegram.converter.chat;

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

import webservices.telegram.model.chat.ChatPhoto;

public class ChatPhotoConverter extends AbstractHttpMessageConverter<ChatPhoto> {

	public ChatPhotoConverter() {
		super(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == ChatPhoto.class;
	}

	@Override
	protected ChatPhoto readInternal(Class<? extends ChatPhoto> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(ChatPhoto user, HttpOutputMessage response)
			throws IOException, HttpMessageNotWritableException {
		try {
			InputStream in = user.getData().getBinaryStream();
			int fileLength = in.available();
			String mimeType = URLConnection.guessContentTypeFromName(user.getName());
			MimeType media = MimeType.valueOf(mimeType);
			response.getHeaders().setContentType(new MediaType(media.getType(), media.getSubtype()));
			response.getHeaders().setContentLength(fileLength);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("filename=\"%s\"", user.getName());
			response.getHeaders().add(headerKey, headerValue);
			IOUtils.copy(in, response.getBody());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

}
