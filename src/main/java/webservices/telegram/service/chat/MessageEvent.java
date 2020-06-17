package webservices.telegram.service.chat;

import java.time.Instant;
import java.util.Collection;

import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.chat.Message;
import webservices.telegram.model.user.User;

public class MessageEvent {

	private String type;
	private Message message;
	private Chat chat;
	private Instant creationTime;
	private Collection<User> recipients;

	public MessageEvent(String type, Message message, Chat chat, Instant creationTime, Collection<User> recipients) {
		super();
		this.type = type;
		this.message = message;
		this.chat = chat;
		this.creationTime = creationTime;
		this.recipients = recipients;
	}

	public String getType() {
		return type;
	}

	public Chat getChat() {
		return chat;
	}

	public Message getMessage() {
		return message;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public Collection<User> getRecipients() {
		return recipients;
	}

	@Override
	public String toString() {
		return "MessageEvent [type=" + type + ", message=" + message + ", chat=" + chat + ", creationTime="
				+ creationTime + ", recipients=" + recipients + "]";
	}
	
}
