package webservices.telegram.converter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import webservices.telegram.model.user.User;
import webservices.telegram.model.user.UserBuilder;

public class UserJsonMapper {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		User user1 = new UserBuilder().id(23l).login("skoriy").firstName("Serhii").lastName("Skoriy")
				.createdAt(Instant.now()).description("hello,world").hasPhoto(false).build();

		User user2 = new UserBuilder().id(23l).login("skoriy").firstName("Serhii").lastName("Skoriy")
				.createdAt(Instant.now()).description("hello,world").hasPhoto(false).build();

		User user3 = new UserBuilder().id(23l).login("skoriy").firstName("Serhii").lastName("Skoriy")
				.createdAt(Instant.now()).description("hello,world").hasPhoto(false).build();

		Collection<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		JSONObject json = new JSONObject();
		json.put("users", toJSONArray(users));
		System.out.println(json.toJSONString());
	}

	public static String toJSON(User user) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", user.getId());
		map.put("login", user.getLogin());
		map.put("firstName", user.getFirstName());
		map.put("lastName", user.getLastName());
		map.put("description", user.getDescription());
		map.put("creationTime", user.getCreatedAt().toString());
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
	public static JSONArray toJSONArray(Collection<User> users) {
		JSONArray array = new JSONArray();
		for (User user : users) {
			array.add(toJSONObject(user));
		}
		return array;
	}

}
