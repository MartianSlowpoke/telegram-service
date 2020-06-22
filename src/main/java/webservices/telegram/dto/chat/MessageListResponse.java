package webservices.telegram.dto.chat;

import java.util.Collection;

import webservices.telegram.model.chat.Message;

public class MessageListResponse {

	private Collection<Message> messages;

	public MessageListResponse(Collection<Message> messages) {
		super();
		this.messages = messages;
	}

	public Collection<Message> getMessages() {
		return messages;
	}

}
