package webservices.telegram.model.chat;

import java.time.Instant;

import webservices.telegram.model.user.User;

public class Message {

	private Long messageId;
	private User sender;
	private String content;
	private MessageFile file;
	private Long chatId;
	private Instant createdAt;
	private Boolean isRead;

	public Message() {
	}

	public Message(User sender, String content, Instant createdAt, Long chatId) {
		this(null, sender, content, null, createdAt);
		this.chatId = chatId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFile(MessageFile file) {
		this.file = file;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Message(User sender, String content, MessageFile file, Instant createdAt) {
		this(null, sender, content, file, createdAt);
	}

	public Message(Long messageId, User sender, String content, Instant createdAt) {
		this(messageId, sender, content, null, createdAt);
	}

	public Message(Long messageId, User sender, String content, MessageFile file, Instant createdAt) {
		this.messageId = messageId;
		this.sender = sender;
		this.content = content;
		this.file = file;
		this.createdAt = createdAt;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public Long getMessageId() {
		return messageId;
	}

	public User getSender() {
		return sender;
	}

	public String getContent() {
		return content;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public MessageFile getFile() {
		return file;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public boolean hasFile() {
		if (file == null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", sender=" + sender + ", content=" + content + ", file=" + file
				+ ", chatId=" + chatId + ", createdAt=" + createdAt + "]";
	}

}
