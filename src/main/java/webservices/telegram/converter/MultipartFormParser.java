package webservices.telegram.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.HttpInputMessage;

public class MultipartFormParser implements UploadContext {

	private Map<String, FileItem> items;
	private InputStream in;
	private String encoding;
	private String contentType;
	private long contentLength;

	public MultipartFormParser() {
		items = new HashMap<>();
	}

	public void parse(HttpInputMessage inputMessage) throws FileUploadException, IOException {
		encoding = inputMessage.getHeaders().getContentType().getParameter("charset");
		contentType = inputMessage.getHeaders().getContentType().toString();
		contentLength = inputMessage.getHeaders().getContentLength();
		in = inputMessage.getBody();
		final FileItemFactory factory = new DiskFileItemFactory();
		FileUpload upload = new FileUpload(factory);
		List<FileItem> fileItems = upload.parseRequest(this);
		for (FileItem item : fileItems) {
			items.put(item.getFieldName(), item);
		}
	}

	public FileItem getFileItem(String key) {
		return items.get(key);
	}

	public String getFileName(String key) {
		FileItem item = getFileItem(key);
		if (item == null)
			return null;
		return item.getName();
	}

	public String get(String key) {
		FileItem item = items.get(key);
		if (item == null)
			return null;
		return item.getString();
	}

	@Override
	public String getCharacterEncoding() {
		return encoding;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public int getContentLength() {
		return -1;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return in;
	}

	@Override
	public long contentLength() {
		return contentLength;
	}

}
