package webservices.telegram.model.user;

import java.time.Instant;

public class UserBuilder {

	protected Long id;
	protected String login;
	protected String firstName;
	protected String lastName;
	protected String description;
	protected UserPhoto photo;
	protected Instant lastSeen;
	protected Boolean isDeleted;
	protected Boolean isOnline;
	protected Instant createdAt;
	protected Boolean hasPhoto;

	public UserBuilder() {
	}

	public UserBuilder id(Long id) {
		this.id = id;
		return this;
	}

	public UserBuilder login(String login) {
		this.login = login;
		return this;
	}

	public UserBuilder firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public UserBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public UserBuilder description(String description) {
		this.description = description;
		return this;
	}

	public UserBuilder photo(UserPhoto photo) {
		this.photo = photo;
		return this;
	}

	public UserBuilder lastSeen(Instant lastSeen) {
		this.lastSeen = lastSeen;
		return this;
	}

	public UserBuilder isOnline(Boolean isOnline) {
		this.isOnline = isOnline;
		return this;
	}

	public UserBuilder isDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
		return this;
	}

	public UserBuilder createdAt(Instant createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public UserBuilder hasPhoto(Boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
		return this;
	}

	public User build() {
		return new User(this);
	}

}
