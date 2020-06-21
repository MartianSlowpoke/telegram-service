package webservices.telegram.converter.chat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.dto.chat.ParticipantRequest;
import webservices.telegram.exception.BadRequestDataException;

public class JSONParticipantConverter extends AbstractHttpMessageConverter<ParticipantRequest> {

	public JSONParticipantConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == ParticipantRequest.class;
	}

	@Override
	protected ParticipantRequest readInternal(Class<? extends ParticipantRequest> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser
					.parse(IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8));
			Long userId = (Long) json.get("userId");
			ParticipantRequest request = new ParticipantRequest(userId);
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestDataException(e.getMessage());
		}
	}

	@Override
	protected void writeInternal(ParticipantRequest t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

	}

}
