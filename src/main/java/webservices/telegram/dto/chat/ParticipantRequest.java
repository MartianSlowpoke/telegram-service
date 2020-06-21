package webservices.telegram.dto.chat;

public class ParticipantRequest {

	private Long userId;

	public ParticipantRequest(Long userId) {
		super();
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

}
