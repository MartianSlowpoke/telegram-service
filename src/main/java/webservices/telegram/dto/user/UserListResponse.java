package webservices.telegram.dto.user;

import java.util.Collection;

import webservices.telegram.model.user.User;

public class UserListResponse {

	private Collection<User> users;

	public UserListResponse(Collection<User> users) {
		super();
		this.users = users;
	}

	public Collection<User> getUsers() {
		return users;
	}

}
