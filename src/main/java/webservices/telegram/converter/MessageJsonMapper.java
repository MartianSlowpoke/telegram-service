package webservices.telegram.converter;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import webservices.telegram.model.chat.Message;
import webservices.telegram.service.chat.MessageEvent;

public class MessageJsonMapper {

	@SuppressWarnings("unchecked")
	public static JSONObject messageToJson(Message message) {
		JSONObject json = new JSONObject();
		json.put("id", message.getMessageId());
		json.put("content", message.getContent());
		json.put("sender", UserJsonMapper.toJSONObject(message.getSender()));
		json.put("isRead", message.getIsRead());
		json.put("createdAt", message.getCreatedAt().toString());
		if (message.hasFile()) {
			String URL = "http://localhost:8080/telegram/chats/" + message.getChatId() + "/" + message.getMessageId()
					+ "/file";
			json.put("file", URL);
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	public static String messagesToJson(Collection<Message> messages) {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		for (Message m : messages) {
			array.add(messageToJson(m));
		}
		json.put("messages", array);
		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public static JSONObject eventToJson(MessageEvent event) {
		JSONObject json = new JSONObject();
		json.put("type", event.getType());
		json.put("createdAt", event.getCreationTime().toString());
		json.put("message", MessageJsonMapper.messageToJson(event.getMessage()));
		json.put("chat", ChatJsonMapper.chatToJson(event.getChat()));
		return json;
	}

}
