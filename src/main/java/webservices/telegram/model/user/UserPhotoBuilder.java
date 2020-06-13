package webservices.telegram.model.user;

import java.sql.Blob;

public class UserPhotoBuilder {

	protected String fileName;
	protected Blob fileData;

	public UserPhotoBuilder() {

	}

	public UserPhotoBuilder fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public UserPhotoBuilder fileData(Blob fileData) {
		this.fileData = fileData;
		return this;
	}

	public UserPhoto build() {
		return new UserPhoto(this);
	}

}
