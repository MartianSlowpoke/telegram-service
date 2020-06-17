package webservices.telegram.service.chat;

public class ChatServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	private Integer httpCode;
	private String message;

	public ChatServiceException(Integer httpCode, String message) {
		super();
		this.httpCode = httpCode;
		this.message = message;
	}

	public Integer getHttpCode() {
		return httpCode;
	}

	public String getMessage() {
		return message;
	}

}
