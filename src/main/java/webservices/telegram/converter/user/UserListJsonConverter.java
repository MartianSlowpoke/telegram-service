package webservices.telegram.converter.user;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import webservices.telegram.dto.user.UserListResponse;
import webservices.telegram.model.user.User;

public class UserListJsonConverter extends AbstractHttpMessageConverter<UserListResponse> {

	public UserListJsonConverter() {
		super(MediaType.APPLICATION_JSON);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == UserListResponse.class;
	}

	@Override
	protected UserListResponse readInternal(Class<? extends UserListResponse> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void writeInternal(UserListResponse t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		for (User user : t.getUsers()) {
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
			array.add(new JSONObject(map));
		}
		json.put("users", array);
		outputMessage.getBody().write(json.toJSONString().getBytes());
	}

}
