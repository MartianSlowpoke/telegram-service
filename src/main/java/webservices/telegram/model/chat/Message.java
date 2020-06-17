package webservices.telegram.model.chat;

import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import webservices.telegram.model.user.User;

public class Message {

	private Long messageId;
	private User sender;
	private String content;
	private MessageFile file;
	private Long chatId;
	private Instant createdAt;

	public enum Property {

		CONTENT, FILE, CHAT_ID;

		public static Property fromPartName(String name) {
			for (Property p : Property.values()) {
				String lowName = name.toLowerCase();
				String lowP = p.toString().toLowerCase();
				if (lowName.equals(lowP))
					return p;
			}
			throw new IllegalArgumentException("couldn't find a property with such given name [" + name + "]");
		}
	}

	public static void main(String... args) throws SerialException, SQLException {
		Map<Property, Object> map = new HashMap<>();
		map.put(Property.CONTENT, "Hello,World");
		map.put(Property.CHAT_ID, Long.valueOf("22"));
		byte[] b = new byte[2];
		b[0] = 2;
		b[1] = 3;
		Blob blob = new SerialBlob(b);
		MessageFile file = new MessageFile("my name", blob);
		map.put(Property.FILE, file);
		Message msg = new Message(map);
		System.out.println(msg.toString());
	}

	public Message(Map<Property, Object> map) {
		for (Property p : map.keySet()) {
			switch (p) {
			case CONTENT:
				this.content = (String) map.get(p);
				break;
			case FILE:
				this.file = (MessageFile) map.get(p);
				break;
			case CHAT_ID:
				this.chatId = (Long) map.get(p);
				break;
			}
		}
		this.createdAt = Instant.now();
	}

	public Message(User sender, String content, Instant createdAt, Long chatId) {
		this(null, sender, content, null, createdAt);
		this.chatId = chatId;
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

	public boolean hasFile() {
		if (file == null)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", sender=" + sender + ", content=" + content + ", file=" + file
				+ ", chatId=" + chatId + ", createdAt=" + createdAt + "]";
	}

}
