package webservices.telegram.dto.user;

import webservices.telegram.model.user.Authentication;
import webservices.telegram.model.user.User;

public class UserRegistrationRequest {

	private User user;
	private Authentication auth;

	public UserRegistrationRequest(User user, Authentication auth) {
		super();
		this.user = user;
		this.auth = auth;
	}

	public User getUser() {
		return user;
	}

	public Authentication getAuth() {
		return auth;
	}

}
