package webservices.telegram.converter;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import webservices.telegram.model.chat.Chat;
import webservices.telegram.service.chat.ChatEvent;

public class ChatJsonMapper {

	@SuppressWarnings("unchecked")
	public static JSONObject chatToJson(Chat chat) {
		JSONObject json = new JSONObject();
		json.put("id", chat.getChatId());
		json.put("type", chat.getType());
		json.put("lastMessage", null);
		json.put("createdAt", chat.getCreatedAt().toString());
		json.put("participiants", UserJsonMapper.toJSONArray(chat.getParticipiants()));
		return json;
	}

	@SuppressWarnings("unchecked")
	public static String chatsToJson(Collection<Chat> chats) {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		for (Chat chat : chats) {
			JSONObject temp = chatToJson(chat);
			array.add(temp);
		}
		json.put("chats", array);
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public static JSONObject eventToJson(ChatEvent event) {
		JSONObject json = new JSONObject();
		json.put("type", event.getType());
		json.put("creationTime", event.getCreationTime().toString());
		json.put("chat", chatToJson(event.getChat()));
		return json;
	}

}
