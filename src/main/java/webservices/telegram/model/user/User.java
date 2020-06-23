package webservices.telegram.model.user;

import java.time.Instant;

public class User {

	private Long id;
	private String login;
	private String firstName;
	private String lastName;
	private String description;
	private UserPhoto photo;
	private Instant lastSeen;
	private Boolean isDeleted;
	private Boolean isOnline;
	private Instant createdAt;

	protected User(UserBuilder builder) {
		this.id = builder.id;
		this.login = builder.login;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.description = builder.description;
		this.photo = builder.photo;
		this.lastSeen = builder.lastSeen;
		this.isDeleted = builder.isDeleted;
		this.isOnline = builder.isOnline;
		this.createdAt = builder.createdAt;
	}

	public Long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getDescription() {
		return description;
	}

	public UserPhoto getPhoto() {
		return photo;
	}

	public Instant getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Instant lastSeen) {
		this.lastSeen = lastSeen;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPhoto(UserPhoto photo) {
		this.photo = photo;
	}

	public boolean hasPhoto() {
		if (photo == null)
			return false;
		return true;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", login=" + login + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", description=" + description + ", photo=" + photo + ", lastSeen=" + lastSeen + ", isDeleted="
				+ isDeleted + ", isOnline=" + isOnline + ", createdAt=" + createdAt + "]";
	}

}
