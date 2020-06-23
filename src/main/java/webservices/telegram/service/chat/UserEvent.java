package webservices.telegram.service.chat;

import java.time.Instant;
import java.util.Collection;

import webservices.telegram.model.user.User;

public class UserEvent {

	private String type;
	private User user;
	private Instant creationTime;
	private String description;
	private Collection<User> recipients;

	public UserEvent(String type, User user, Instant creationTime, String description, Collection<User> recipients) {
		super();
		this.type = type;
		this.user = user;
		this.creationTime = creationTime;
		this.description = description;
		this.recipients = recipients;
	}

	public String getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public String getDescription() {
		return description;
	}

	public Collection<User> getRecipients() {
		return recipients;
	}

}
