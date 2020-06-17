package webservices.telegram.model.chat;

import java.sql.Blob;

public class MessageFile {

	private String name;
	private Blob data;

	public MessageFile(String name, Blob data) {
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
