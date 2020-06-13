package webservices.telegram.converter.user;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.model.user.User;

public class JsonUserConverter extends AbstractHttpMessageConverter<User> {

	public JsonUserConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		if (clazz == User.class)
			return true;
		return false;
	}

	@Override
	protected User readInternal(Class<? extends User> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(User user, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", user.getId());
		map.put("login", user.getLogin());
		map.put("firstName", user.getFirstName());
		map.put("lastName", user.getLastName());
		map.put("description", user.getDescription());
		map.put("creationTime", user.getCreatedAt().toString());
		map.put("hasPhoto", user.hasPhoto());
		if (user.hasPhoto()) {
			map.put("photo", "http://localhost:8080/telegram/users/" + user.getId() + "/photo");
		}
		JSONObject json = new JSONObject(map);
		outputMessage.getBody().write(json.toJSONString().getBytes());
	}

}
