package webservices.telegram.model.user;

import java.sql.Blob;

public class UserPhoto {

	private String fileName;
	private Blob fileData;

	protected UserPhoto(UserPhotoBuilder builder) {
		this.fileName = builder.fileName;
		this.fileData = builder.fileData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Blob getFileData() {
		return fileData;
	}

	public void setFileData(Blob fileData) {
		this.fileData = fileData;
	}

	@Override
	public String toString() {
		return "UserPhoto [fileName=" + fileName + ", fileData=" + fileData + "]";
	}

}
