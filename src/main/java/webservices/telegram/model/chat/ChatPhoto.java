package webservices.telegram.model.chat;

import java.sql.Blob;

public class ChatPhoto {

	private String name;
	private Blob data;

	public ChatPhoto(String name, Blob data) {
		super();
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public Blob getData() {
		return data;
	}

}
