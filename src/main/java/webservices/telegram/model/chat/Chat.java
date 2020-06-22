package webservices.telegram.model.chat;

import java.time.Instant;
import java.util.Collection;

import webservices.telegram.model.user.User;

public class Chat {

	private Long chatId;
	private String type;
	private String name;
	private String description;
	private User creator;
	private Message lastMessage;
	private Collection<User> participiants;
	private Instant createdAt;
	private ChatPhoto photo;

	public Chat() {
	}

	public Chat(String type, Instant createdAt, Collection<User> participiants) {
		this.type = type;
		this.createdAt = createdAt;
		this.participiants = participiants;
	}

	public Chat(Long chatId, String type, String name, Message lastMessage, Instant createdAt,
			Collection<User> participiants) {
		this.chatId = chatId;
		this.type = type;
		this.name = name;
		this.lastMessage = lastMessage;
		this.createdAt = createdAt;
		this.participiants = participiants;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChatId(Chat this, Long chatId) {
		this.chatId = chatId;
	}

	public void setParticipiants(Collection<User> participiants) {
		this.participiants = participiants;
	}

	public Long getChatId() {
		return chatId;
	}

	public String getType() {
		return type;
	}

	public Message getLastMessage() {
		return lastMessage;
	}

	public Collection<User> getParticipiants() {
		return participiants;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public ChatPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(ChatPhoto photo) {
		this.photo = photo;
	}

	public boolean hasPhoto() {
		if (photo == null)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Chat [chatId=" + chatId + ", type=" + type + ", lastMessage=" + lastMessage + ", participiants="
				+ participiants + ", createdAt=" + createdAt + "]";
	}

}
