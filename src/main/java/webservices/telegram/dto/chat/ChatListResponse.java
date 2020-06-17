package webservices.telegram.dto.chat;

import java.util.Collection;

import webservices.telegram.model.chat.Chat;

public class ChatListResponse {

	private Collection<Chat> chats;

	public ChatListResponse(Collection<Chat> chats) {
		super();
		this.chats = chats;
	}

	public Collection<Chat> getChats() {
		return chats;
	}

}
