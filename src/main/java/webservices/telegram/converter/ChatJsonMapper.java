package webservices.telegram.converter;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import webservices.telegram.model.chat.Chat;
import webservices.telegram.service.chat.ChatEvent;
import webservices.telegram.service.chat.ParticipantEvent;

public class ChatJsonMapper {

	@SuppressWarnings("unchecked")
	public static JSONObject chatToJson(Chat chat) {
		JSONObject json = new JSONObject();
		json.put("id", chat.getChatId());
		json.put("type", chat.getType());
		json.put("name", chat.getName());
		json.put("description", chat.getDescription());
		json.put("creator", UserJsonMapper.toJSONObject(chat.getCreator()));
		if (chat.getLastMessage() != null)
			json.put("lastMessage", MessageJsonMapper.messageToJson(chat.getLastMessage()));
		else
			json.put("lastMessage", null);
		if (chat.hasPhoto())
			json.put("photo", "http://localhost:8080/telegram/chats/" + chat.getChatId() + "/photo");
		else
			json.put("photo", null);
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
	public static String eventToJson(ParticipantEvent event) {
		JSONObject json = new JSONObject();
		json.put("type", event.getType());
		json.put("chat", ChatJsonMapper.chatToJson(event.getChat()));
		json.put("user", UserJsonMapper.toJSONObject(event.getUser()));
		json.put("creationTime", event.getCreationTime().toString());
		json.put("description", event.getDescription());
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
