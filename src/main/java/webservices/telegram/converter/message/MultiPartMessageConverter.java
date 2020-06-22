package webservices.telegram.converter.message;

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
import webservices.telegram.exception.BadRequestDataException;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.chat.MessageFile;

public class MultiPartMessageConverter extends AbstractHttpMessageConverter<Message> {

	public MultiPartMessageConverter() {
		super(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Message.class;
	}

	@Override
	protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			MultipartFormParser form = new MultipartFormParser();
			form.parse(inputMessage);
			Message msg = new Message();
			msg.setIsRead(Boolean.valueOf(form.get("isRead")));
			msg.setContent(form.get("content"));
			if (form.getFileItem("file") != null) {
				if (form.getFileItem("file").getSize() != 0 && form.getFileName("file") != null) {
					msg.setFile(
							new MessageFile(form.getFileName("file"), new SerialBlob(form.getFileItem("file").get())));
				}
			}
			return msg;
		} catch (FileUploadException | IOException | SQLException e) {
			e.printStackTrace();
			throw new BadRequestDataException(e.getMessage());
		}
	}

	@Override
	protected void writeInternal(Message t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
	}

}
