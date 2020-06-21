package webservices.telegram.service.chat;

import java.time.Instant;
import java.util.Collection;

import webservices.telegram.model.chat.Chat;
import webservices.telegram.model.user.User;

public class ParticipantEvent {

	private String type;
	private Chat chat;
	private User user;
	private Collection<User> recipients;
	private Instant creationTime;
	private String description;

	public ParticipantEvent(String type, Chat chat, User user, Collection<User> recipients, Instant creationTime,
			String description) {
		super();
		this.type = type;
		this.chat = chat;
		this.user = user;
		this.recipients = recipients;
		this.creationTime = creationTime;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public Chat getChat() {
		return chat;
	}

	public User getUser() {
		return user;
	}

	public Collection<User> getRecipients() {
		return recipients;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public String getDescription() {
		return description;
	}

}
