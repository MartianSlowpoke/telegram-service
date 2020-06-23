package webservices.telegram.converter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import webservices.telegram.model.user.User;
import webservices.telegram.service.chat.UserEvent;

public class UserJsonMapper {

	public static String toJSON(User user) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", user.getId());
		map.put("login", user.getLogin());
		map.put("firstName", user.getFirstName());
		map.put("lastName", user.getLastName());
		map.put("description", user.getDescription());
		if (user.getLastSeen() != null) {
			map.put("lastSeen", user.getLastSeen().toString());
		} else {
			map.put("lastSeen", null);
		}
		map.put("creationTime", user.getCreatedAt().toString());
		map.put("isOnline", user.getIsOnline());
		if (user.hasPhoto()) {
			map.put("photo", "http://localhost:8080/messenger/users/" + user.getId() + "/photo");
		}
		return JSONObject.toJSONString(map);
	}

	@SuppressWarnings("unchecked")
	public static JSONObject toJSONObject(User user) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", user.getId());
		map.put("login", user.getLogin());
		map.put("firstName", user.getFirstName());
		map.put("lastName", user.getLastName());
		map.put("description", user.getDescription());
		map.put("creationTime", user.getCreatedAt().toString());
		map.put("hasPhoto", user.hasPhoto());
		if (user.hasPhoto()) {
			map.put("photo", "http://localhost:8080/messenger/users/" + user.getId() + "/photo");
		}
		JSONObject json = new JSONObject();
		json.putAll(map);
		return json;
	}

	@SuppressWarnings("unchecked")
	public static String eventToJson(UserEvent event) {
		JSONObject json = new JSONObject();
		json.put("type", event.getType());
		json.put("createdAt", event.getCreationTime());
		json.put("user", toJSON(event.getUser()));
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public static JSONArray toJSONArray(Collection<User> users) {
		JSONArray array = new JSONArray();
		for (User user : users) {
			array.add(toJSONObject(user));
		}
		return array;
	}

}
